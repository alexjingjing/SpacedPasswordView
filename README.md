# SpacedPasswordView

![image](https://github.com/alexwtw/SpacedPasswordView/blob/master/verify_code.gif)

## Intrduction
This project is based on [Jungerr/GridPasswordView](https://github.com/Jungerr/GridPasswordView).make some custom changes,like the space between grids.

参考[Jungerr/GridPasswordView](https://github.com/Jungerr/GridPasswordView)做了一些自定义的开发。如输入框之间的间距。

## how to use
1. download the code
2. import the module 'spacedpasswordview' to your project
3. File->Project Structure->dependensies->'+' add dependency to your project
4. add to your xml files

```
<com.liusiming.spacedpasswordview.SpacedPasswordView
        android:id="@+id/id_custom_password"
        android:layout_width="200dp"
        android:layout_height="40dp"
        android:layout_centerHorizontal="true"
        android:layout_alignParentBottom="true"
        app:cpvTextSize="20sp"
        app:cpvTextColor="@color/tangerine"
        app:cpvPasswordLength="4"
        app:cpvLineColor="@android:color/black"
        app:cpvGridColor="@android:color/white"
        app:cpvLineWidth="2dp"
        app:cpvNeedSpace="true" />
```
### add on attr
`cpvNeedCursor` true if u need cursor.

`cpvSpaceWeight` the weight of the space, defalut is 0.2(max is 0.5).

`cpvNeedSpace` true if u need space.

`cpvSpaceColor` the color of the space, default is white.

[other attr](https://github.com/Jungerr/GridPasswordView)

### some public method
`setPasswordVisibility(boolean visible)` whether user can see the content of the password

`getPassword()` get the input password

`clearPassword()` clear the password

`setPasswordListener(CustomListener mListener)` register a callback to be invoked when password changed.

## Other
If you use this library in your app, please let me know : alex_liu@hunghingtech.com
## Contributing

Yes:) If you found a bug, have an idea how to improve library or have a question, please create new issue or comment existing one. If you would like to contribute code fork the repository and send a pull request.
