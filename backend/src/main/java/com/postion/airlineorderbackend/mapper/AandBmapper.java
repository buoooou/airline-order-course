package com.postion.airlineorderbackend.mapper;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * Only for study and test, please ignore this file.
 */
@Mapper
public interface AandBmapper {

  public static AandBmapper INSTANCE = Mappers.getMapper(AandBmapper.class);

  @Mappings({
      @Mapping(source = "memberA", target = "member1"),
      @Mapping(source = "memberB", target = "member2")
  })
  public ClassB caToCb1(ClassA ca);

  @Mappings({
      @Mapping(target = "member1", expression = "java(ca.getMemberA())"),
      @Mapping(target = "member2", expression = "java(ca.getMemberB())")
  })
  public ClassB caToCb2(ClassA ca);

  default public List<ClassB> caListToCbList(List<ClassA> caList) {
    if (caList == null) {
      return null;
    }
    List<ClassB> cbList = new ArrayList<ClassB>();
    caList.forEach(ca -> cbList.add(caToCb1(ca)));
    return cbList;
  };

}
