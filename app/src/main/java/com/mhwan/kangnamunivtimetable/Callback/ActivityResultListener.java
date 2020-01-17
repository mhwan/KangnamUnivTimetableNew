package com.mhwan.kangnamunivtimetable.Callback;

import android.content.Intent;

public interface ActivityResultListener {
    void onActivityResults(int requestcode, int resultcode, Intent data);
}
