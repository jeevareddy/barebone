package com.barebone.app

import androidx.appcompat.widget.AppCompatSpinner

class User(
    var name: com.google.android.material.textfield.TextInputEditText,
    var gender: AppCompatSpinner,
    var email: com.google.android.material.textfield.TextInputEditText,
    var mobile: com.google.android.material.textfield.TextInputEditText,
    var password: com.google.android.material.textfield.TextInputEditText,
    var confirmPassword: com.google.android.material.textfield.TextInputEditText
)