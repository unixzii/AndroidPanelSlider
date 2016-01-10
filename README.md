# PanelSlider
A simple library to make panel collapsable, with animation and more.

![](https://raw.githubusercontent.com/unixzii/AndroidPanelSlider/master/image/screenshot.gif)

## Features
  * Optimized for ListView and ScrollView
  * Smooth and flexible
  * Support listener callback
  * Super easy to use, easy to import(Only one `.java` file)

## Requirements
  * Any IDE can work
  * Android 2.3+

## Installation
Just copy the `PanelSlider.java` file into your project and all done.

## Usage
The view is actually a `FrameLayout`, and you can just use it as `FrameLayout`.

For example, if you want to collapse a ListView, you can just write these:

```xml
<com.cyandev.panelslider.PanelSlider
            android:id="@+id/panelSlider"
            android:layout_width="match_parent"
            android:layout_height="match_parent">

        <ListView
                android:id="@+id/listView"
                android:layout_width="match_parent"
                android:layout_height="match_parent"/>

</com.cyandev.panelslider.PanelSlider>
```

And in code, you just need to set an initial height, that's all.

```java
PanelSlider slider = (PanelSlider) findViewById(R.id.panelSlider);
slider.setInitialHeight(500);
```

If you like, you can set a listener to listen the change of `progress`:

```java
slider.setOnProgressListener(...);
```

> **One thing need to be attention.**
> 
> Make a little change in your `ListView`(or `ScrollView`), add an `OnTouchListener` to them or override their `onTouchEvent` method. When the list scrolled to its bound, you should call `PanelSlider#allowInterceptChildTouchEvent()` function. It's better to call `PanelSlider#disallowInterceptChildTouchEvent()` function when `ListView` begin to be touched.

Just take a look at the demo for details.

## Contribution
If you have any issues or need help please do not hesitate to create an issue ticket. And if you have something awesome to add to it and welcome to make a pull request. Thanks for supporting.

## Contact Me
  * Email: [unixzii@gmail.com](mailto:unixzii@gmail.com)
  * Weibo: [@unixzii](http://weibo.com/2834711045/profile)
  * Twitter: [@unixzii](https://twitter.com/unixzii)
  * Google+: [杨弘宇](https://plus.google.com/u/0/114460726879043684053)
