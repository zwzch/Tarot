package com.zwzch.fool.repo;

import com.zwzch.fool.repo.model.PhysicalDB;

public interface IRepo {
    /* 获得pdb */
    public PhysicalDB getPhysicalDB(String name) throws Exception;
}
