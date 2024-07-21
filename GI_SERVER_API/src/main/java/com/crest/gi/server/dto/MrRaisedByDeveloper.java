package com.md.gi.server.dto;

import lombok.Data;

/**
 * This class used to send response for mr raised by each developer
 */
@Data
public class MrRaisedByDeveloper {
    String username;
    String name;
    Long count;
}
