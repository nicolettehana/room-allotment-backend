package sad.sras.dto.master;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sad.sras.models.master.Category;
import sad.sras.models.master.YearRange;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FirmsDTO {
	
	private Long id;
    private String firm;
    private List<Category> categories;
    private List<YearRange> yearRanges;

}
