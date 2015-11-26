package com.advantage.online.store.services;

import com.advantage.online.store.dao.attribute.AttributeRepository;
import com.advantage.online.store.dao.attribute.DefaultAttributeRepository;
import com.advantage.online.store.model.attribute.Attribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AttributeService {
    @Autowired
    AttributeRepository attributeRepository;

    @Transactional(readOnly = true)
    public List<Attribute> getAllAttribute(){
        return attributeRepository.getAll();
    }

    @Transactional(readOnly = true)
    public  Attribute getAttributeByName(String name) {
        return attributeRepository.get(name);
    }

    @Transactional
    public Attribute createAttribute(String name) {
        return attributeRepository.create(name.toUpperCase());
    }
}
