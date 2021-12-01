package edu.temple.projectblz;

import java.io.IOException;

public interface LocationAddress {
    String showAddress(double lat, double lon) throws IOException;
}
