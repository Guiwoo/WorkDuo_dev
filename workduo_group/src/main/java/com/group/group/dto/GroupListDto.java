package com.group.group.dto;

import com.core.domain.group.dto.GroupDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupListDto {

    Page<GroupDto> groupList;
}
