package com.yifan.butterfly;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
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

import static com.yifan.butterfly.C.*;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.STATIC;

@AutoService(Processor.class)
public final class ButterflyProcessor extends AbstractProcessor {

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

        // Assemble Butterfly models
        List<ButterflyModel> activityModels = new ArrayList<>();
        for (Element activity : roundEnv.getElementsAnnotatedWith(BActivity.class)) {
            if (!isValidActivity(activity)) return false;

            BActivity annotation = activity.getAnnotation(BActivity.class);

            ButterflyModel model = new ButterflyModel();
            model._Activity = (TypeElement) activity;
            model._Result = annotation.hasResult();

            List<? extends Element> enclosedElements = activity.getEnclosedElements();
            for (Element element : enclosedElements) {
                BExtra extra = element.getAnnotation(BExtra.class);
                if (extra != null) {
                    if (!isValidExtra(element)) return false;
                    String extraAlias = extra.value().isEmpty() ? element.getSimpleName().toString() : extra.value();
                    model._ExtraElement.add((VariableElement) element);
                    model._ExtraAlias.add(extraAlias);
                }
            }

            activityModels.add(model);
        }

        // generate helper
        for (ButterflyModel model : activityModels) {
            TypeElement activity = model._Activity;
            boolean requireStartForResult = model._Result;
            ClassName activityClassName = (ClassName) ClassName.get(activity.asType());
            ClassName helperName = ClassName.get(HELPER_PACKAGE_NAME, activity.getSimpleName().toString() + HELPER_CLASS_SUFFIX);
            String packageName = _elementUtils.getPackageOf(activity).getQualifiedName().toString();

            // Helper class definition
            TypeSpec.Builder helper = TypeSpec.classBuilder(helperName)
                    .superclass(ACTIVITY_HELPER)
                    .addModifiers(PUBLIC, FINAL)
                    .addJavadoc("Generated by Butterfly, do not modified!\n\n");

            // generate with<extra> methods
            for (int i = 0; i < model._ExtraElement.size(); i++) {
                VariableElement extra = model._ExtraElement.get(i);
                String extraAlias = model._ExtraAlias.get(i);
                String extraId = extra.getSimpleName().toString();
                TypeName extraType = TypeName.get(extra.asType());

                MethodSpec.Builder withExtra = MethodSpec.methodBuilder(getSnakeCaseWithExtraMethodName(extraAlias))
                        .returns(helperName)
                        .addModifiers(PUBLIC)
                        .addJavadoc("Set the " + extraAlias + " extra")
                        .addParameter(extraType, extraAlias)
                        .addStatement("_intent.putExtra(\"$L\", $L)", extraId, extraAlias)
                        .addStatement("return this");
                helper.addMethod(withExtra.build());
            }

            MethodSpec.Builder withFlags = MethodSpec.methodBuilder("withFlags")
                    .addAnnotation(Override.class)
                    .addModifiers(PUBLIC)
                    .returns(helperName)
                    .addJavadoc("Equivalent to intent.setFlags()")
                    .addParameter(TypeName.INT, "flags")
                    .addStatement("super.withFlags(flags)")
                    .addStatement("return this");
            helper.addMethod(withFlags.build());

            MethodSpec.Builder withAnim = MethodSpec.methodBuilder("withAnim")
                    .addAnnotation(Override.class)
                    .addModifiers(PUBLIC)
                    .returns(helperName)
                    .addJavadoc("Set enter and exit animation")
                    .addParameter(TypeName.INT, "enterRes")
                    .addParameter(TypeName.INT, "exitRes")
                    .addStatement("super.withAnim(enterRes, exitRes)")
                    .addStatement("return this");
            helper.addMethod(withAnim.build());

            MethodSpec.Builder withOptions = MethodSpec.methodBuilder("withOptions")
                    .addAnnotation(Override.class)
                    .addModifiers(PUBLIC)
                    .returns(helperName)
                    .addJavadoc("start options")
                    .addParameter(BUNDLE, "options")
                    .addStatement("super.withOptions(options)")
                    .addStatement("return this");
            helper.addMethod(withOptions.build());

            MethodSpec.Builder asIntent = MethodSpec.methodBuilder("asIntent")
                    .addAnnotation(Override.class)
                    .addModifiers(PUBLIC)
                    .returns(INTENT)
                    .addJavadoc("Return this as intent")
                    .addParameter(CONTEXT, "context")
                    .addStatement("_intent.setComponent(new $T(context, $T.class))",
                            COMPONENT_NAME, activityClassName)
                    .addStatement("return _intent");
            helper.addMethod(asIntent.build());

            // generate helper.go methods
            MethodSpec.Builder go = MethodSpec.methodBuilder("go")
                    .addModifiers(PUBLIC)
                    .addParameter(CONTEXT, "context")
                    .addStatement("beforeStart(context)")
                    .addStatement("_intent.setComponent(new $T(context, $T.class))",
                            COMPONENT_NAME, activityClassName)
                    .addStatement("context.startActivity(_intent, _options)");
            helper.addMethod(go.build());

            // emit helper.goForResult method
            if (requireStartForResult) {
                MethodSpec.Builder goForResult = MethodSpec.methodBuilder("goForResult")
                        .addModifiers(PUBLIC)
                        .addParameter(ACTIVITY, "activity")
                        .addParameter(TypeName.INT, "requestCode")
                        .addStatement("beforeStartForResult(activity, requestCode)")
                        .addStatement("_intent.setComponent(new $T(activity, $T.class))",
                                COMPONENT_NAME, activityClassName)
                        .addStatement("activity.startActivityForResult(_intent, requestCode, _options)", ACTIVITY);
                helper.addMethod(goForResult.build());
            }

            // for debugging
            helper.addJavadoc("ButterflyModel: \n").addJavadoc(model.toString());

            try {
                JavaFile.builder(HELPER_PACKAGE_NAME, helper.build())
                        .build()
                        .writeTo(_filer);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // generate getHelper method for Butterfly class
            MethodSpec.Builder getHelper = MethodSpec.methodBuilder("to" + model._Activity.getSimpleName())
                    .addModifiers(PUBLIC, STATIC)
                    .returns(helperName)
                    .addStatement("return new $T()", helperName);
            butterfly.addMethod(getHelper.build());
        }

        // Generate extra binder
        for (ButterflyModel model : activityModels) {
            TypeElement activity = model._Activity;
            ClassName activityClassName = (ClassName) ClassName.get(activity.asType());
            String packageName = _elementUtils.getPackageOf(activity).getQualifiedName().toString();
            // Binder wants to be within the same package as the activity, such that protected / package-private members can be accessed
            ClassName binderName = ClassName.get(packageName, activity.getSimpleName() + BINDER_CLASS_SUFFIX);

            // Binder class definition
            TypeSpec.Builder binder = TypeSpec.classBuilder(binderName)
                    .superclass(ParameterizedTypeName.get(BINDER, activityClassName))
                    .addModifiers(PUBLIC, FINAL)
                    .addJavadoc("Generated by Butterfly, do not modified!\n");

            MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
                    .addModifiers(PUBLIC)
                    .addParameter(activityClassName, "target")
                    .addStatement("super(target)");

            MethodSpec.Builder bind = MethodSpec.methodBuilder("bind")
                    .addAnnotation(Override.class)
                    .addModifiers(PUBLIC)
                    .addParameter(TypeName.get(activity.asType()), "activity")
                    .addStatement("super.bind(activity)");

            for (VariableElement element : model._ExtraElement) {
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
                    bind.addStatement("activity.$L = _intent.$L(\"$L\", $L)",
                            identifier, parsePrimitiveTypeToMethodName(typeMirror),
                            identifier, defaultVal);
                } else if (typeKind == TypeKind.ARRAY) {
                    TypeMirror componentType = ((ArrayType) typeMirror).getComponentType();
                    if (componentType.getKind().isPrimitive()) {
                        bind.addStatement("activity.$L = _intent.$L(\"$L\")",
                                identifier, parsePrimitiveTypeToMethodName(typeMirror),
                                identifier);
                    } else if (isTypeString(componentType)) {
                        bind.addStatement("activity.$L = (String[])_intent.getStringArrayExtra(\"$L\")",
                                identifier, identifier);
                    } else if (isTypeParcelable(componentType)) {
                        bind.addStatement("activity.$L = ($L[])_intent.getParcelableArrayExtra(\"$L\")",
                                identifier, componentType, identifier);
                    }
                } else if (isTypeString(typeMirror)) {
                    bind.addStatement("activity.$L = _intent.getStringExtra(\"$L\")",
                            identifier, identifier);
                } else if (isTypeSerializable(typeMirror)) {
                    bind.addStatement("activity.$L = ($L)_intent.getSerializableExtra(\"$L\")",
                            identifier, typeMirror, identifier);
                } else if (isTypeParcelable(typeMirror)) {
                    bind.addStatement("activity.$L = _intent.getParcelableExtra(\"$L\")",
                            identifier, identifier);
                }
            }

            binder.addMethod(constructor.build());
            binder.addMethod(bind.build());

            try {
                JavaFile.builder(packageName, binder.build())
                        .build()
                        .writeTo(_filer);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // generate bind method for Butterfly class
            MethodSpec.Builder getBinder = MethodSpec.methodBuilder("bind")
                    .addModifiers(PUBLIC, STATIC)
                    .addParameter(activityClassName, "target", FINAL)
                    .returns(binderName)
                    .addStatement("return new $T(target)", binderName);
            butterfly.addMethod(getBinder.build());
        }

        try {
            JavaFile.builder(BUTTERFLY_PACKAGE_NAME, butterfly.build())
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

    private boolean isValidActivity(Element activity) {
        if (isSubtypeOfType(activity.asType(), _elementUtils.getTypeElement(ACTIVITY.reflectionName()).asType())) {
            return true;
        } else {
            TypeElement enclosingElement = (TypeElement) activity.getEnclosingElement();
            error(activity, "@%s annotated element %s must be subtypes of Activity. (%s.%s)",
                    BActivity.class.getSimpleName(), activity.getSimpleName(), enclosingElement.getQualifiedName(), activity.getSimpleName());
            return false;
        }
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

    private boolean isSubtypeOfType(TypeMirror subType, TypeMirror type) {
        return _typeUtils.isSubtype(subType, type);
    }

    private boolean isTypeEqual(TypeMirror thisType, TypeElement thatType) {
        return _typeUtils.isSameType(thisType, thisType);
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

    private String getSnakeCaseWithExtraMethodName(String extraName) {
        if (Character.isLowerCase(extraName.charAt(0))) {
            char[] letters = extraName.toCharArray();
            letters[0] = Character.toUpperCase(letters[0]);
            extraName = String.copyValueOf(letters);
        }
        return "with" + extraName;
    }

}
