package com.chad.videochatapp.Listeners;

import com.chad.videochatapp.Models.User;

public interface UserListener {

    void initiateVideoMeeting(User user);

    void initiateAudioMeeting(User user);
}
