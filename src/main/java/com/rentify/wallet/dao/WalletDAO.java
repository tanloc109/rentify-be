package com.rentify.wallet.dao;

import com.rentify.base.dao.BaseDAO;
import com.rentify.wallet.entity.Wallet;
import jakarta.ejb.Stateless;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;
import java.util.Optional;

@Stateless
public class WalletDAO extends BaseDAO<Wallet> {

    public WalletDAO() {
        super(Wallet.class);
    }

    public Optional<Wallet> findByUserId(Long userId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Wallet> cq = cb.createQuery(Wallet.class);
        Root<Wallet> root = cq.from(Wallet.class);
        cq.select(root).where(cb.equal(root.get("user").get("id"), userId));
        TypedQuery<Wallet> query = entityManager.createQuery(cq);
        List<Wallet> result = query.getResultList();
        return result.stream().findFirst();
    }
}
