package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.WalletRequest;
import co.com.bancolombia.api.dto.WalletResponse;
import co.com.bancolombia.model.wallet.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {PocketMapper.class})
public interface WalletMapper {

	// Map WalletRequest DTO to domain Wallet model. id and pocketes are managed elsewhere.
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "pocketes", ignore = true)
	Wallet toModel(WalletRequest request);

	// Map domain Wallet model to WalletResponse DTO. Pocketes are mapped via PocketMapper.
	WalletResponse toResponse(Wallet wallet);
}
