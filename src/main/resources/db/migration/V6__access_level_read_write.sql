UPDATE person_object_type_access SET access_level = 'READ' WHERE access_level = 'VIEW';

DELETE t1 FROM person_object_type_access t1
INNER JOIN person_object_type_access t2
  ON t1.person_id = t2.person_id
  AND t1.object_type = t2.object_type
  AND t1.access_level = t2.access_level
  AND t1.id > t2.id;
