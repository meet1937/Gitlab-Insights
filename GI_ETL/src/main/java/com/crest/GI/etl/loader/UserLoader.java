package com.md.GI.etl.loader;

import com.md.GI.etl.mapper.GitlabMapper;
import com.md.GI.etl.model.User;

import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserLoader {

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
  private final String query =
      ".users(uid,integration_id,id, username,name, state,record_created_at,record_updated_at)\n"
          + "                VALUES(:uid,:integrationId,:id,:username,:name,:state,:recordCreatedAt,:recordUpdatedAt)"
          + "                on conflict(id,integration_id)"
          + "                do"
          + "                UPDATE"
          + "                SET username=:username, name=:name, state=:state,record_updated_at=:recordUpdatedAt;";
  private final String addSchemaName = "insert into ";
  GitlabMapper userMapper;

  @Autowired
  public UserLoader(
      GitlabMapper userMapper, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
    this.userMapper = userMapper;
    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
  }

  public void insertUsers(List<User> userDtos, UUID integrationId, String schemaName) {

    List<Map<String, Object>> batchOfInputs = new ArrayList<>();
    Map<String, Object>[] inputs = new HashMap[batchOfInputs.size()];
    for(int idx = 0; idx<userDtos.size() ; idx++) {
        User user=userDtos.get(idx);
        if(user==null){
        System.out.println(idx);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("uid", UUID.randomUUID());
        map.put("integrationId", integrationId);
        map.put("id", user.getId());
        map.put("username", user.getUsername());
        map.put("name", user.getName());
        map.put("state", user.getState());
        map.put("recordCreatedAt", Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC)));
        map.put("recordUpdatedAt", Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC)));
        batchOfInputs.add(map);
    }
    namedParameterJdbcTemplate.batchUpdate(addSchemaName + schemaName + query, batchOfInputs.toArray(inputs));
    log.info("User Data Saved Successfully to Database");
  }
}
