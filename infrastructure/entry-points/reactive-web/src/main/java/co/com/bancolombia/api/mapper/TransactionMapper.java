package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.TransactionRequest;
import co.com.bancolombia.api.dto.TransactionResponse;
import co.com.bancolombia.model.transaction.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

	// Map TransactionRequest DTO to domain Transaction model. id and createdAt are managed elsewhere.
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	Transaction toModel(TransactionRequest request);

	// Map domain Transaction model to TransactionResponse DTO.
	TransactionResponse toResponse(Transaction transaction);
}
