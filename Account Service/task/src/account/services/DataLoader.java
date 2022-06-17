package account.services;


import account.models.UserGroup;
import account.models.UserRole;
import account.repositories.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class DataLoader {

    private GroupRepository groupRepository;

    @Autowired
    public DataLoader(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
        createRoles();
    }

    private void createRoles() {
        try {
            groupRepository.save(new UserGroup(UserRole.ADMINISTRATOR));
            groupRepository.save(new UserGroup(UserRole.USER));
            groupRepository.save(new UserGroup(UserRole.ACCOUNTANT));
            groupRepository.save(new UserGroup(UserRole.AUDITOR));
        } catch (Exception e) {

        }
    }


    public UserGroup getByRole(UserRole role) {
        return this.groupRepository.findByCode(role).get();
    }
}
