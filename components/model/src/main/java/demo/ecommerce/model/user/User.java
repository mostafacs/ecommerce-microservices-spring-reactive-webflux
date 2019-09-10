package demo.ecommerce.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

/**
 * @author Mostafa Albana
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
@Table("users")
public class User {
    @Id
    private Long id;

    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String roles;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    @Column("create_date")
    private Date createDate;

    @Column("update_date")
    private Date updateDate;

    @Column("last_login")
    private Date lastLogin;


}
