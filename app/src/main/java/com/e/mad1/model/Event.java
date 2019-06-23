package com.e.mad1.model;

import java.util.Date;

public interface Event {
    long getId();
    void setId(long id);

    String getTitle();

    Date getStartDate();

    Date getEndDate();

    String getVenue();

    String getLocation();
    // leave as it since no google map to insert location yet
    // Im not sure which format to store location as i have no idea which format google map
    //  value will return, so i decide not to touch it instead, if use string
    //   can i refer it just like title and venue section ?
}
