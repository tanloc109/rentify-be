package com.vaccinex.dao;

import com.vaccinex.base.dao.AbstractDao;
import com.vaccinex.pojo.Reaction;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

/**
 * Implementation of the ReactionDao interface
 */
@ApplicationScoped
public class ReactionDaoImpl extends AbstractDao<Reaction, Integer> implements ReactionDao {

    public ReactionDaoImpl() {
        super(Reaction.class);
    }

    @Override
    @Transactional
    public Reaction save(Reaction reaction) {
        if (reaction.getId() == null) {
            entityManager.persist(reaction);
            return reaction;
        } else {
            return entityManager.merge(reaction);
        }
    }
}