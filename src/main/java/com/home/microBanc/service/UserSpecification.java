package com.home.microBanc.service;

import com.home.microBanc.dto.UserSearchDTO;
import com.home.microBanc.modal.Users;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class UserSpecification {

    public static Specification<Users> searchUsers(UserSearchDTO searchDTO) {
        return new Specification<Users>() {
            @Override
            public Predicate toPredicate(Root<Users> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                var predicate = criteriaBuilder.conjunction();

                if (StringUtils.isNotEmpty(searchDTO.getName())) {
                    predicate = criteriaBuilder.and(
                            predicate,
                            criteriaBuilder.like(root.get("name"), searchDTO.getName() + "%")
                    );
                }

                if (StringUtils.isNotEmpty(searchDTO.getEmail())) {
                    var emailJoin = root.join("emailData");
                    predicate = criteriaBuilder.and(
                            predicate,
                            criteriaBuilder.equal(emailJoin.get("email"), searchDTO.getEmail())
                    );
                }

                if (StringUtils.isNotEmpty(searchDTO.getPhone())) {
                    var phoneJoin = root.join("phoneData");
                    predicate = criteriaBuilder.and(
                            predicate,
                            criteriaBuilder.equal(phoneJoin.get("phone"), searchDTO.getPhone())
                    );
                }


                if (searchDTO.getDateOfBirth() != null) {
                    predicate = criteriaBuilder.and(
                            predicate,
                            criteriaBuilder.greaterThan(root.get("dateOfBirth"), searchDTO.getDateOfBirth())
                    );
                }

                return predicate;
            }
        };
    }
}