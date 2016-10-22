package com.liusiming.spacedpasswordview;

/**
 * @author Jungly
 * @mail jungly.ik@gmail.com
 * @date 15/3/21 16:20
 */
interface PasswordView {

    //void setError(String error);

    String getPassWord();

    void clearPassword();

    void setPassword(String password);

    void setPasswordVisibility(boolean visible);

    void togglePasswordVisibility();

    void setOnPasswordChangedListener(CustomPasswordView.OnPasswordChangedListener listener);

    void setPasswordType(PasswordType passwordType);
}
