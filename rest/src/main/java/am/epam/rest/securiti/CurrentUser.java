package am.epam.rest.securiti;

import org.springframework.security.core.authority.AuthorityUtils;

public class CurrentUser extends org.springframework.security.core.userdetails.User {


    private am.epam.common.model.User user;

    public CurrentUser(am.epam.common.model.User user) {

        super(user.getEmail(), user.getPassword(), AuthorityUtils.createAuthorityList(user.getUserType().name()));
        this.user = user;
    }

    public am.epam.common.model.User getUser() {
        return user;
    }

}
