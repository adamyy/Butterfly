package com.yifan.butterfly.processors;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.yifan.butterfly.BActivity;
import com.yifan.butterfly.BExtra;
import com.yifan.butterfly.helper.ButterflyModel;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import static com.yifan.butterfly.C.ACTIVITY;
import static com.yifan.butterfly.C.COMPONENT_NAME;
import static com.yifan.butterfly.C.CONTEXT;
import static com.yifan.butterfly.C.INTENT;
import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

@AutoService(Processor.class)
public class ButterflyProcessor extends AbstractProcessor {

    private static final ClassName BUTTERFLY = ClassName.get("com.yifan.butterfly", "Butterfly");
    private static final String PACKAGE_NAME = "com.yifan.butterfly";
    private static final String HELPER_PACKAGE_NAME = PACKAGE_NAME + ".helper";
    private static final String HELPER_CLASS_SUFFIX = "$$Helper";
    private static final ClassName ACTIVITY_HELPER = ClassName.get("com.yifan.butterfly.router", "ActivityHelper");

    private Elements _elementUtils;
    private Types _typeUtils;
    private Filer _filer;
    private Messager _messager;

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
            types.add(annotation.getCanonicalName());
        }
        return types;
    }

    private Set<Class<? extends Annotation>> getSupportedAnnotations() {
        Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();
        annotations.add(BActivity.class);
        annotations.add(BExtra.class);
        return annotations;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        _elementUtils = processingEnv.getElementUtils();
        _typeUtils = processingEnv.getTypeUtils();
        _filer = processingEnv.getFiler();
        _messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnv) {

        // Butterfly class
        TypeSpec.Builder butterfly = TypeSpec.classBuilder(BUTTERFLY)
                .addJavadoc("This class is generated and should not be modified \n")
                .addModifiers(PUBLIC, FINAL);

        // emit activity helper class
        for (Element activity : roundEnv.getElementsAnnotatedWith(BActivity.class)) {

            String activityAlias = !activity.getAnnotation(BActivity.class).alias().isEmpty() ?
                    activity.getAnnotation(BActivity.class).alias() : activity.getSimpleName().toString();

            ClassName helperName = ClassName.get(HELPER_PACKAGE_NAME, activityAlias + HELPER_CLASS_SUFFIX);
            ClassName activityClassName = (ClassName) ClassName.get(activity.asType());

            TypeSpec.Builder helper = TypeSpec.classBuilder(helperName)
                    .superclass(ACTIVITY_HELPER)
                    .addModifiers(PUBLIC, FINAL)
                    .addJavadoc("This class is generated and should not be modified\n");

            // Build the activity model
            ButterflyModel model = new ButterflyModel();
            model._Actvity = (TypeElement) activity;
            model._ActivityId = activityAlias;
            List<? extends Element> enclosedElements = activity.getEnclosedElements();
            for (Element element : enclosedElements) {
                BExtra extra = element.getAnnotation(BExtra.class);
                if (extra != null) {
                    if (!isValidExtra(element)) return false;

                    String extraId = element.getSimpleName().toString();
                    String extraAlias = extra.alias().isEmpty() ? extraId : extra.alias();
                    model._ExtraElements.add((VariableElement) element);
                    model._ExtraIds.add(extraAlias);

                    TypeName extraType = TypeName.get(element.asType());

                    FieldSpec.Builder extraField = FieldSpec.builder(extraType, extraId, PRIVATE);
                    helper.addField(extraField.build());

                    MethodSpec.Builder withExtra = MethodSpec.methodBuilder("with" + extraAlias)
                            .returns(helperName)
                            .addModifiers(PUBLIC)
                            .addJavadoc("Set the " + extraAlias + " extra")
                            .addParameter(extraType, extraAlias)
                            .addStatement("_intent.putExtra(\"$L\", $L)", extraId, extraAlias)
                            .addStatement("this.$L = $L", extraId, extraAlias)
                            .addStatement("return this");
                    helper.addMethod(withExtra.build());
                }
            }

            // emit helper.start method
            MethodSpec.Builder start_context = MethodSpec.methodBuilder("start")
                    .addAnnotation(Override.class)
                    .addModifiers(PUBLIC)
                    .addParameter(CONTEXT, "from")
                    .addStatement("_intent.setComponent(new $T(from, $T.class))",
                            COMPONENT_NAME, activityClassName)
                    .addStatement("from.startActivity(_intent, _options)");

            MethodSpec.Builder start_activity = MethodSpec.methodBuilder("start")
                    .addAnnotation(Override.class)
                    .addModifiers(PUBLIC)
                    .addParameter(ACTIVITY, "from")
                    .addStatement("_intent.setComponent(new $T(from, $T.class))",
                            COMPONENT_NAME, activityClassName)
                    .addStatement("from.startActivity(_intent, _options)");

            // emit helper.startForResult method
            MethodSpec.Builder start_for_result = MethodSpec.methodBuilder("startForResult")
                    .addAnnotation(Override.class)
                    .addModifiers(PUBLIC)
                    .addParameter(ACTIVITY, "from")
                    .addParameter(TypeName.INT, "requestCode")
                    .addStatement("_intent.setComponent(new $T(from, $T.class))",
                            COMPONENT_NAME, activityClassName)
                    .addStatement("from.startActivityForResult(_intent, requestCode, _options)");

            helper.addMethod(start_for_result.build());
            helper.addMethod(start_context.build());
            helper.addMethod(start_activity.build());

            helper.addJavadoc("ButterflyModel: \n")
                    .addJavadoc(model.toString());

            try {
                JavaFile.builder(HELPER_PACKAGE_NAME, helper.build())
                        .build()
                        .writeTo(_filer);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // emit butterfly.getHelper method
            MethodSpec.Builder getHelper = MethodSpec.methodBuilder("get" + helperName.simpleName())
                    .addModifiers(PUBLIC, STATIC)
                    .returns(helperName)
                    .addStatement("return new $L()", helperName);

            // emit butterfly.bind method
            MethodSpec.Builder bind = MethodSpec.methodBuilder("bind")
                    .addModifiers(PUBLIC, STATIC)
                    .addParameter(TypeName.get(activity.asType()), "activity")
                    .addStatement("$T intent = activity.getIntent()", INTENT);

            for (VariableElement element : model._ExtraElements) {
                String identifier = element.getSimpleName().toString();
                TypeMirror typeMirror = element.asType();
                TypeKind typeKind = typeMirror.getKind();

                if (typeKind.isPrimitive()) {
                    String defaultVal;
                    switch (typeKind) {
                        case BOOLEAN:
                            defaultVal = "false";
                            break;
                        case BYTE:
                            defaultVal = "(byte) 0";
                            break;
                        case SHORT:
                            defaultVal = "(short) 0";
                            break;
                        case CHAR:
                            defaultVal = "(char) 0";
                            break;
                        default:
                            defaultVal = "0";
                            break;
                    }
                    bind.addStatement("activity.$L = intent.$L(\"$L\", $L)",
                            identifier, parsePrimitiveTypeToMethodName(typeMirror),
                            identifier, defaultVal);
                } else if (typeKind == TypeKind.ARRAY){
                    TypeMirror componentType = ((ArrayType) typeMirror).getComponentType();
                    if (componentType.getKind().isPrimitive()) {
                        bind.addStatement("activity.$L = intent.$L(\"$L\")",
                                identifier, parsePrimitiveTypeToMethodName(typeMirror),
                                identifier);
                    } else if (isTypeString(componentType)) {
                        bind.addStatement("activity.$L = (String[])intent.getStringArrayExtra(\"$L\")",
                                identifier, identifier);
                    } else if (isTypeParcelable(componentType)) {
                        bind.addStatement("activity.$L = ($L[])intent.getParcelableArrayExtra(\"$L\")",
                                identifier, componentType, identifier);
                    }
                } else if (isTypeString(typeMirror)) {
                    bind.addStatement("activity.$L = intent.getStringExtra(\"$L\")",
                            identifier, identifier);
                } else if (isTypeSerializable(typeMirror)){
                    bind.addStatement("activity.$L = ($L)intent.getSerializableExtra(\"$L\")",
                            identifier, typeMirror, identifier);
                } else if (isTypeParcelable(typeMirror)) {
                    bind.addStatement("activity.$L = intent.getParcelableExtra(\"$L\")",
                            identifier, identifier);
                }

            }

            butterfly.addMethod(getHelper.build());
            butterfly.addMethod(bind.build());
        }

        try {
            JavaFile.builder(PACKAGE_NAME, butterfly.build())
                    .build()
                    .writeTo(_filer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * See if the extra type is valid and can be put on a bundle<br>
     * Stuff that can be put in bundle:
     * <ul>
     * <li>Binder</li>
     * <li>Bundle</li>
     * <li>Byte, ByteArray</li>
     * <li>Char, CharArray</li>
     * <li>CharSequence, CharSequenceArray, CharSequenceArrayList</li>
     * <li>Float, FloatArray</li>
     * <li>Parcelable, ParcelableArray, ParcelableArrayList</li>
     * <li>Serializable</li>
     * <li>Short, ShortArray</li>
     * <li>Size</li>
     * <li>SizeF</li>
     * <li>SparseParcelableArray</li>
     * <li>StringArrayList</li>
     * </ul>
     * <p>
     * Stuff that can be put as an extra (intent.putExtra()):
     * <ul>
     * <li>Parcelable, Parcelable[], ParcelableArrayList</li>
     * <li>byte, byte[]</li>
     * <li>boolean, boolean[]</li>
     * <li>char, char[]</li>
     * <li>short, short[]</li>
     * <li>int, int[], IntegerArrayList</li>
     * <li>float, float[]</li>
     * <li>double, double[]</li>
     * <li>long, long[]</li>
     * <li>CharSequence, CharSequence[], CharSequenceArrayList</li>
     * <li>String, String[], StringArrayList</li>
     * <li>Bundle</li>
     * <li>Serializable</li>
     * </ul>
     *
     * @param extra
     * @return true if valid
     */
    private boolean isValidExtra(Element extra) {
        boolean valid;

        TypeMirror extraType = extra.asType();
        TypeElement enclosingElement = (TypeElement) extra.getEnclosingElement();

        if (isInaccessibleViaGeneratedCode(BExtra.class, extra)) {
            error(extra, "@%s %s must not be private or protected. (%s.%s)", BExtra.class.getSimpleName(),
                    extra.getSimpleName(), enclosingElement.getQualifiedName(), extra.getSimpleName());
            return false;
        }

        if (extraType.getKind() == TypeKind.ARRAY) {
            extraType = ((ArrayType) extraType).getComponentType();
        }

        if (extraType.getKind().isPrimitive()) {
            valid = true;
        } else if (isTypeParcelable(extraType)) {
            valid = true;
        } else if (isTypeSerializable(extraType)) {
            valid = true;
        } else {
            valid = false;
        }

        if (!valid) {
            error(extra, "@%s %s must be of valid extra types or an array of those types. (%s.%s)", BExtra.class.getSimpleName(),
                    extra.getSimpleName(), enclosingElement.getQualifiedName(), extra.getSimpleName());
        }

        return valid;
    }

    private boolean isTypeParcelable(TypeMirror type) {
        return implementsInterface(type, _elementUtils.getTypeElement("android.os.Parcelable").asType());
    }

    private boolean isTypeSerializable(TypeMirror type) {
        return implementsInterface(type, _elementUtils.getTypeElement("java.io.Serializable").asType());
    }

    private boolean isTypeString(TypeMirror type) {
        return implementsInterface(type, _elementUtils.getTypeElement("java.lang.String").asType());
    }

    private boolean implementsInterface(TypeMirror myType, TypeMirror desiredInterface) {
        return _typeUtils.isAssignable(myType, desiredInterface);
    }

    private boolean isInaccessibleViaGeneratedCode(Class<? extends Annotation> annotationClass, Element element) {
        boolean hasError = false;
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();

        // Verify method modifiers.
        Set<Modifier> modifiers = element.getModifiers();
        if (modifiers.contains(PRIVATE) || modifiers.contains(STATIC)) {
            error(element, "@%s %s must not be private or static. (%s.%s)",
                    annotationClass.getSimpleName(), enclosingElement.getQualifiedName(),
                    element.getSimpleName());
            hasError = true;
        }

        // Verify containing type.
        if (enclosingElement.getKind() != CLASS) {
            error(enclosingElement, "@%s %s may only be contained in classes. (%s.%s)",
                    annotationClass.getSimpleName(), enclosingElement.getQualifiedName(),
                    element.getSimpleName());
            hasError = true;
        }

        // Verify containing class visibility is not private.
        if (enclosingElement.getModifiers().contains(PRIVATE)) {
            error(enclosingElement, "@%s %s may not be contained in private classes. (%s.%s)",
                    annotationClass.getSimpleName(), enclosingElement.getQualifiedName(),
                    element.getSimpleName());
            hasError = true;
        }

        return hasError;
    }

    private void error(Element element, String message, Object... args) {
        printMessage(Diagnostic.Kind.ERROR, element, message, args);
    }

    private void note(Element element, String message, Object... args) {
        printMessage(Diagnostic.Kind.NOTE, element, message, args);
    }

    private void printMessage(Diagnostic.Kind kind, Element element, String message, Object[] args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        _messager.printMessage(kind, message, element);
    }

    private String parsePrimitiveTypeToMethodName(TypeMirror typeMirror) {
        String[] names = typeMirror.toString().split("\\.");
        String temp = names[names.length - 1];
        temp = temp.replace("[]", "Array");
        StringBuilder simpleName = new StringBuilder(temp);
        if (Character.isLowerCase(simpleName.charAt(0))) {
            simpleName.replace(0, 1, String.valueOf(Character.toUpperCase(simpleName.charAt(0))));
        }
        simpleName.insert(0, "get");
        simpleName.append("Extra");
        return simpleName.toString();
    }

}
