package ar.edu.itba.paw.interfaces.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductImageResponse implements Serializable {
    private long productImageId;
    private String filename;
}
