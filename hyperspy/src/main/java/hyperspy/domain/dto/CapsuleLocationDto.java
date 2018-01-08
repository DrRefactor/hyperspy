package hyperspy.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class CapsuleLocationDto {
    private Integer capsuleId;
    private Integer infratuctureElementId;
    private BigDecimal coorX;
    private BigDecimal coorY;
}
