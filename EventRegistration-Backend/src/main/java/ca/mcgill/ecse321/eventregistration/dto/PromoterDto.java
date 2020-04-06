package ca.mcgill.ecse321.eventregistration.dto;

import java.util.Collections;
import java.util.List;

public class PromoterDto extends PersonDto {
	private String name;
	private List<EventDto> promotes;

	public PromoterDto() {
	}

	@SuppressWarnings("unchecked")
	public PromoterDto(String name) {
		this(name, Collections.EMPTY_LIST);
	}

	public PromoterDto(String name, List<EventDto> events) {
		this.name = name;
		this.promotes = events;
	}

	public String getName() {
		return name;
	}

	public List<EventDto> getPromotes() {
		return promotes;
	}

	public void setPromotes(List<EventDto> promotes) {
		this.promotes = promotes;
	}
}


