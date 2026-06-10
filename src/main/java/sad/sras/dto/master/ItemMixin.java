package sad.sras.dto.master;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import sad.sras.models.master.SubItems;

public abstract class ItemMixin {

	@JsonIgnore
    private List<SubItems> subItems;
}
