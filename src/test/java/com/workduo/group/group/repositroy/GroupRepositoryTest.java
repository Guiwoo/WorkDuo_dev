package com.workduo.group.group.repositroy;

import com.workduo.area.siggarea.entity.SiggArea;
import com.workduo.configuration.jpa.JpaAuditingConfiguration;
import com.workduo.group.group.entity.Group;
import com.workduo.group.group.repository.GroupRepository;
import com.workduo.group.group.type.GroupStatus;
import com.workduo.group.groupjoinmember.repository.GroupJoinMemberRepository;
import com.workduo.sport.sport.entity.Sport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.time.LocalDateTime;

import static com.workduo.group.group.type.GroupStatus.GROUP_STATUS_ING;

@DataJpaTest
@Transactional
@Rollback(value = false)
@Import({JpaAuditingConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class GroupRepositoryTest {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    GroupJoinMemberRepository groupJoinMemberRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("group save")
    @Transactional
    public void groupSave() throws Exception {
        // given
        Group group = Group.builder()
                .siggArea(SiggArea.builder().id(1).build())
                .sport(Sport.builder().id(1).build())
                .name("test")
                .limitPerson(10)
                .introduce("test")
                .thumbnailPath("test")
                .groupStatus(GROUP_STATUS_ING)
                .build();

        groupRepository.save(group);
        // when

        // then
    }

    @Test
    @DisplayName("update group join member status cancel")
    @Transactional
    public void updateGroupJoinMemberStatusCancel() throws Exception {
        // given
        Group group = Group.builder()
                .id(2L)
                .siggArea(SiggArea.builder().id(1).build())
                .sport(Sport.builder().id(1).build())
                .name("test")
                .limitPerson(10)
                .introduce("test")
                .thumbnailPath("test")
                .groupStatus(GROUP_STATUS_ING)
                .build();

        groupJoinMemberRepository
                .updateGroupJoinMemberStatusCancel(group);
        // when

        // then
    }
}
