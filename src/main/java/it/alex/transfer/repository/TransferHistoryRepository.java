package it.alex.transfer.repository;

import it.alex.transfer.entity.TransferHistoryEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class TransferHistoryRepository {

    private final Session session;

    public static TransferHistoryRepository newInstance(Session session) {
        return new TransferHistoryRepository(session);
    }


    public Optional<TransferHistoryEntity> findHistoryById(Long id) {
        return Optional.ofNullable(session.get(TransferHistoryEntity.class, id));
    }


    public TransferHistoryEntity save(TransferHistoryEntity history) {
        session.saveOrUpdate(history);
        return history;
    }

    public List<TransferHistoryEntity> findHistoryBySourceId(Long sourceId) {

        final Query<TransferHistoryEntity> query = session
                .createQuery("from TransferHistoryEntity " +
                        "where fromAccount=:sourceId", TransferHistoryEntity.class)
                .setParameter("sourceId", sourceId);

        return Optional.ofNullable(query.getResultList()).orElseGet(Collections::emptyList);
    }

    public List<TransferHistoryEntity> findHistoryByTargetId(Long targetId) {

        final Query<TransferHistoryEntity> query = session
                .createQuery("from TransferHistoryEntity " +
                        "where toAccount=:targetId", TransferHistoryEntity.class)
                .setParameter("targetId", targetId);

        return Optional.ofNullable(query.getResultList()).orElseGet(Collections::emptyList);
    }
}
