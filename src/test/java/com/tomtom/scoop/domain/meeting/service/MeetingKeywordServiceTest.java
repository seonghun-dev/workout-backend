package com.tomtom.scoop.domain.meeting.service;

import com.tomtom.scoop.domain.meeting.model.entity.MeetingType;
import com.tomtom.scoop.domain.meeting.repository.MeetingTypeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("[API][Service] 모임 키워드 관련 테스트")
public class MeetingKeywordServiceTest {

    @Mock
    MeetingTypeRepository meetingTypeRepository;

    @InjectMocks
    private MeetingKeywordService meetingKeywordService;

    @Nested
    @DisplayName("[API][Service] 모임 키워드 조회 테스트")
    class FindAllMeetingKeywords {

        @Nested
        @DisplayName("[API][Service] 모임 키워드 조회 성공 테스트")
        class Success {
            @Test
            @DisplayName("[API][Service] 모임 키워드 조회 성공 테스트")
            void findAllMeetingKeywords() {
                var meetingTypes = List.of(
                        new MeetingType(1L, "Fun"),
                        new MeetingType(2L, "Play")
                );

                when(meetingTypeRepository.findAll()).thenReturn(meetingTypes);

                var result = meetingKeywordService.findAllMeetingKeywords();

                Assertions.assertThat(result).isNotNull();
                Assertions.assertThat(result).hasSize(2);
                Assertions.assertThat(result.get(0).getId()).isEqualTo(1L);
                Assertions.assertThat(result.get(0).getKeyword()).isEqualTo("Fun");
                Assertions.assertThat(result.get(1).getId()).isEqualTo(2L);
                Assertions.assertThat(result.get(1).getKeyword()).isEqualTo("Play");
            }

        }

    }


}
