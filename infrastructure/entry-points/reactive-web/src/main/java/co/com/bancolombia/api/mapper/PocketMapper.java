package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.PocketRequest;
import co.com.bancolombia.api.dto.PocketResponse;
import co.com.bancolombia.model.pocket.Pocket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {TransactionMapper.class})
public interface PocketMapper {

	// Map PocketRequest DTO to domain Pocket model. id and transactions are managed elsewhere,
	// so we ignore them here to avoid accidental overwrites.
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "transactions", ignore = true)
	Pocket toModel(PocketRequest request);

	// Map domain Pocket model to PocketResponse DTO. Transactions are mapped via TransactionMapper.
	PocketResponse toResponse(Pocket pocket);
}
