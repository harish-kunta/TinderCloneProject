package com.harish.tinder.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;

public class AgeCalculator {
    //TODO remove these
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int calculateAge(long dobUnix) {
        LocalDate birthDate =
                Instant.ofEpochSecond(dobUnix).atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate currentDate = LocalDate.now();
        if ((birthDate != null) && (currentDate != null)) {
            return Period.between(birthDate, currentDate).getYears();
        } else {
            return 0;
        }
    }
}
