package com.zwzch.tarot.temperance.segment.dao;

import com.zwzch.tarot.temperance.segment.dao.mapper.SegmentMapper;
import com.zwzch.tarot.temperance.segment.dao.model.TemperanceModel;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;
import java.util.List;


public class SegmentDaoImpl implements ISegmentDao {

    final SqlSessionFactory sqlSessionFactory;

    SegmentMapper segmentMapper;

    public SegmentDaoImpl(DataSource dataSource) {
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.addMapper(SegmentMapper.class);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    }


    public List<String> getAllTags() {
        SqlSession sqlSession = sqlSessionFactory.openSession(false);
        try {
            return sqlSession.selectList(SegmentMapper.class.getName() + ".getAllTags");
        } finally {
            sqlSession.commit();
        }
    }

    public TemperanceModel updateIndexByTag(String tag) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        TemperanceModel result;
        try {
            sqlSession.update(SegmentMapper.class.getName() + ".updateMaxId", tag);
            result = sqlSession.selectOne(SegmentMapper.class.getName() + ".getByTag", tag);
            sqlSession.commit();
        } finally {
            sqlSession.commit();
        }
        return result;
    }

    @Override
    public TemperanceModel updateMaxIdByCustomStepAndGet(TemperanceModel model) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        TemperanceModel result;
        try {
            sqlSession.update(SegmentMapper.class.getName() + ".updateMaxIdByCustomStep", model);
            result = sqlSession.selectOne(SegmentMapper.class.getName() + ".getByTag", model.getTag());
            sqlSession.commit();
        } finally {
            sqlSession.commit();
        }
        return result;
    }


}
