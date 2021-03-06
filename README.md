# Butterfly
An android library designed to make moving between activities easy

![](https://travis-ci.org/adamyy/Butterfly.svg?branch=master)

<3

- [JavaPoet](https://github.com/square/javapoet)
- [A Jake Wharton talk on annotation processing](https://www.youtube.com/watch?v=dOcs-NKK-RA&t=2167s)


In Android, let's say you want to start a new `Activity`, maybe pass a few extras in `Intent`, then retrieve these extras in the started `Activity`

You will probably write some code like this:

```java
Intent intent = new Intent(this, SomeOtherActivity.class);
intent.putExtra("someIntExtra", 1);
intent.putExtra("someStringExtra", "hello world");
startActivity(intent);
```

Then in the `onCreate()` of the started `Activity`, to retrieve the extras:

```java
int i = getIntExtra("someIntExtra", 0);
String s = getStringExtra("someSrtingExtra");
```

Which looks kinda tedious. Also you don't really need your brain to write these code, yet if you make some mistakes in writing them you will have to debug them later on. For example in the above code I misspelled the string extra name on purpose, just to give you an idea.

The purpose of Butterfly (I'm a big fan of [Butterknife](https://github.com/JakeWharton/butterknife), don't judge my naming skills) is to make your life easier in this process.


### Sample usage

First annotate the `Activity` class with `@BActivity`, also annotate the 
extra fields you want Butterfly to bind with `@BExtra`. 

```java
@BActivity
public class ExtraActivity extends AppCompatActivity {

    @BExtra(alias = "id") int _intExtra;
    @BExtra(alias = "name") String _stringExtra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //...
        Butterfly.bind(this);
    }
    
}
```

Then these fields annotated with `@BExtra` with be injected after you call `Butterfly.bind(this)` in `onCreate()`

When you wish to launch ExtraActivity with these extras, you do something like this:

```java
public class MainActivity extends AppCompatActivity {
    public void launchExtraActivity() {
        Butterfly.toExtraActivity()
                .withname("ENIVEL RETEP") // Notice the setter name is associated with alias given in @BExtra
                .withid(42069)
                .go(this);
    }
}
```

Simply get the corresponding activity helper from Butterfly, set the extra you want to pass using "with<extraName>" methods,
do "start" to start the activity using extras you set previously.

Currently supported extra types are:

- all primitives and their arrays
- parcelable objects and their arrays
- serializable objects

### Download

It is recommended that you add Butterfly as a git submodule:

- `git submodule add git@github.com:adamyy/Butterfly.git <some repository, e.g. lib/butterfly>`

Then in the directory where you cloned the Butterfly repo, checkout the tag/release you want:

- `git checkout 0.1.0`

Then you can import `butterfly-annotations`, `butterfly-api`, `butterfly-compiler` as library modules in Android Studio.

You can see this [stack overflow question](https://stackoverflow.com/questions/27717728/android-studio-how-to-add-my-own-git-repository-as-a-library-projectsub-module) for troubleshooting.

Then you can add Butterfly as your app dependency in your app level `build.gradle`:

```
dependencies {
	compile project(':butterfly-annotations')
	compile project(':butterfly-api')
	annotationProcessor project(':butterfly-compiler')
	...
}
```

And if you are using Kotlin you can do `kapt project(':butterfly-compiler')`

You might need to configure Android Studio to enable annotation processing.

# License 

Copyright 2017 Yifan Yang

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


