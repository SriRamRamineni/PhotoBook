package com.example.sriram.photoBook;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by sriram on 7/25/2016.
 */
public class UserInfo extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.userinfo,container,false);
        Bundle args = getArguments();
        int userId = args.getInt("userId");
        TextView userText = (TextView) view.findViewById(R.id.username);
        TextView email = (TextView) view.findViewById(R.id.email_user);
        TextView phone = (TextView) view.findViewById(R.id.phone_user);
        TextView street = (TextView)view. findViewById(R.id.street_user);
        TextView suite = (TextView) view.findViewById(R.id.suite_user);
        TextView city = (TextView) view.findViewById(R.id.city_user);
        TextView zipcode = (TextView) view.findViewById(R.id.zip_user);
        TextView company = (TextView)view.findViewById(R.id.company_user);
        UserInformation userInformation = Constants.userInfo.get(userId);
        if (userText != null) {
            userText.setText(userInformation.getUserName());
        }
        if (email != null) {
            email.setText(userInformation.getEmailId());
        }
        if (phone != null) {
            phone.setText(userInformation.getPhoneNumber());
        }
        if (street != null) {
            street.setText(userInformation.getStreet());
        }
        if (suite != null) {
            suite.setText(userInformation.getSuit());
        }
        if (city != null) {
            city.setText(userInformation.getCity());
        }
        if (zipcode != null) {
            zipcode.setText(userInformation.getZipCode());
        }
        if (company != null) {
            company.setText(userInformation.getCompanyName());
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
