package com.core.back9.service;

import com.core.back9.dto.RoomDTO;
import com.core.back9.entity.Building;
import com.core.back9.entity.Room;
import com.core.back9.entity.constant.Status;
import com.core.back9.entity.constant.Usage;
import com.core.back9.mapper.RoomMapper;
import com.core.back9.repository.BuildingRepository;
import com.core.back9.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest
@Transactional
class RoomServiceTest {

	@Mock
	private RoomRepository roomRepository;

	@Mock
	private RoomMapper roomMapper;

	@Mock
	private BuildingRepository buildingRepository;

	@InjectMocks
	private RoomService roomService;

	private Building building;
	private Room room;
	private RoomDTO.Request request;
	private RoomDTO.Response response;
	private RoomDTO.Info info;

	@BeforeEach
	public void initSetting() {
		roomService = new RoomService(buildingRepository, roomRepository, roomMapper);
		building = Building.builder()
		  .name("building name")
		  .address("building address")
		  .zipCode("building zipCode")
		  .build();
		buildingRepository.save(building);
		room = Room.builder()
		  .building(building)
		  .name("room name 1")
		  .floor("room floor 1")
		  .area(84F)
		  .usage(Usage.OFFICES)
		  .build();
		request = RoomDTO.Request.builder()
		  .name("room name")
		  .floor("room floor")
		  .area(84F)
		  .usage(Usage.FINANCIAL_BUSINESSES)
		  .build();
		response = RoomDTO.Response.builder()
		  .id(1L)
		  .name("room name")
		  .floor("room floor")
		  .area(84F)
		  .usage(Usage.FINANCIAL_BUSINESSES)
		  .status(Status.REGISTER)
		  .build();
		info = RoomDTO.Info.builder()
		  .id(1L)
		  .name("room name")
		  .floor("room floor")
		  .area(84F)
		  .usage(Usage.FINANCIAL_BUSINESSES)
		  .status(Status.REGISTER)
		  .createdAt(
			LocalDateTime.of(2024, 5, 5, 15, 55, 55)
		  )
		  .build();
	}

	@DisplayName("호실 등록 성공")
	@Test
	public void givenRequestWhenCreateRoomThenResponse() {
		Room savedRoom = Room.builder()
		  .building(building)
		  .name("room name 1")
		  .floor("room floor 1")
		  .area(84F)
		  .usage(Usage.OFFICES)
		  .build();
		given(roomMapper.toEntity(building.getId(), request)).willReturn(room);
		given(roomRepository.save(room)).willReturn(savedRoom);
		given(roomMapper.toResponse(savedRoom)).willReturn(response);

		RoomDTO.Response result = roomService.create(building.getId(), request);

		assertThat(result).isEqualTo(result);
		verify(roomMapper).toEntity(building.getId(), request);
		verify(roomRepository).save(room);
		verify(roomMapper).toResponse(savedRoom);
	}

	@DisplayName("호실 정보 수정 성공")
	@Test
	public void givenRoomIdAndRequestWhenUpdateRoomDataThenInfo() {
		long updateId = 1L;
		RoomDTO.Request updateRequest = RoomDTO.Request.builder()
		  .name("updated room name")
		  .floor("updated room floor")
		  .build();
		RoomDTO.Info updateInfo = RoomDTO.Info.builder()
		  .name("updated room name")
		  .floor("updated room floor")
		  .build();
		given(roomRepository.getValidRoomWithIdOrThrow(updateId, Status.REGISTER)).willReturn(room);
		given(roomMapper.toInfo(room)).willReturn(updateInfo);

		RoomDTO.Info result = roomService.update(updateId, updateRequest);

		assertThat(result).isEqualTo(updateInfo);
		verify(roomRepository).getValidRoomWithIdOrThrow(updateId, Status.REGISTER);
		verify(roomMapper).toInfo(room);
	}

	@DisplayName("호실 삭제 성공")
	@Test
	public void givenRoomIdWhenDeleteRoomThenSuccessResult() {
		long deleteId = 1L;
		given(roomRepository.getValidRoomWithIdOrThrow(deleteId, Status.REGISTER)).willReturn(room);

		boolean result = roomService.delete(deleteId);

		assertThat(result).isTrue();
		verify(roomRepository).getValidRoomWithIdOrThrow(deleteId, Status.REGISTER);
	}

	@DisplayName("전체 호실 조회 성공")
	@Test
	public void givenBuildingIdWhenSelectAllRoomsThenRoomPageList() {
		long selectedBuildingId = 1L;
		Pageable pageable = PageRequest.of(0, 10);
		Page<Room> roomPage = new PageImpl<>(List.of(room), pageable, 1);
		Page<RoomDTO.Info> roomInfoPage = new PageImpl<>(List.of(info), pageable, 1);

		given(roomRepository.findAllByBuildingIdAndStatus(selectedBuildingId, Status.REGISTER, pageable)).willReturn(roomPage);
		given(roomMapper.toInfo(room)).willReturn(info);

		Page<RoomDTO.Info> result = roomService.selectAll(selectedBuildingId, pageable);

		assertThat(result).isEqualTo(roomInfoPage);
		verify(roomRepository).findAllByBuildingIdAndStatus(selectedBuildingId, Status.REGISTER, pageable);
		verify(roomMapper).toInfo(room);
	}

	@DisplayName("호실 아이디로 조회 성공")
	@Test
	public void givenRoomIdWhenSelectOneRoomThenRoomInfo() {
		long selectedRoomId = 1L;
		given(roomRepository.getValidRoomWithIdOrThrow(selectedRoomId, Status.REGISTER)).willReturn(room);
		given(roomMapper.toInfo(room)).willReturn(info);

		RoomDTO.Info result = roomService.selectOne(selectedRoomId);
		assertThat(result).isEqualTo(info);
		verify(roomRepository).getValidRoomWithIdOrThrow(selectedRoomId, Status.REGISTER);
		verify(roomMapper).toInfo(room);
	}

}