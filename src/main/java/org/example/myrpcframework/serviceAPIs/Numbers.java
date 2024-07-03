package org.example.myrpcframework.serviceAPIs;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Numbers implements Serializable {
    private int number1;
    private int number2;
}
