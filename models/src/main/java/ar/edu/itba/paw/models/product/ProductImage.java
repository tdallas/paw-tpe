package ar.edu.itba.paw.models.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_image")
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "NUMERIC(19,0)")
    private long id;
    private byte[] file;
    private String fileName;

    public ProductImage(final byte[] file, final String filename) {
        this.file = file;
        this.fileName = filename;
    }

}
