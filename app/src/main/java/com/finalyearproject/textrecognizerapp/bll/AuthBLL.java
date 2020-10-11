package com.finalyearproject.textrecognizerapp.bll;

import com.finalyearproject.textrecognizerapp.api.AuthAPI;
import com.finalyearproject.textrecognizerapp.helper.ApiError;
import com.finalyearproject.textrecognizerapp.helper.Retrofit;
import com.finalyearproject.textrecognizerapp.models.Errors;
import com.finalyearproject.textrecognizerapp.models.User;
import com.finalyearproject.textrecognizerapp.response.UserResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class AuthBLL {
    private AuthAPI authAPI;
    private Gson gson;
    private ApiError apiError;
    private AuthBLL.AuthListener authListener;

    public AuthBLL() {

        authAPI = Retrofit.getInstance().create(AuthAPI.class);
        gson = new GsonBuilder().create();
        apiError = new ApiError();
    }

    //user registration function
    public boolean registerUser(User user) {
        boolean isSignUpSuccessful = false;

        Call<UserResponse> signUpCall = authAPI.registerUser(user);

        try {
            Response<UserResponse> signUpResponse = signUpCall.execute();
            if (!signUpResponse.isSuccessful()) {
                apiError = gson.fromJson(signUpResponse.errorBody().string(), ApiError.class);
                authListener.onError(apiError.getErrors());
//                return isSignUpSuccessful;
            } else if (signUpResponse.body().getUser() != null) {
                isSignUpSuccessful = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return isSignUpSuccessful;
    }

    //user login function
    public User loginUser(String email, String password) {
        User user = null;
        Call<UserResponse> loginCall = authAPI.loginUser(email, password);

        try {
            Response<UserResponse> loginResponse = loginCall.execute();
            if (!loginResponse.isSuccessful()) {
                apiError = gson.fromJson(loginResponse.errorBody().string(), ApiError.class);
//                authListener.onError(apiError.getErrors());
//                return user;
            } else if (loginResponse.body().getUser() != null) {
                user = loginResponse.body().getUser();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return user;
    }
    public interface AuthListener {
        void onError(Errors errors);
    }

    public void setAuthListener(AuthListener authListener) {
        this.authListener = authListener;
    }
}
