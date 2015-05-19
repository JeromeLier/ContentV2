SELECT
        MIN(c.id),
        c.email_body,
        t.reason_id,
        t.language_code,
        r.sub_reason_lv1_id,
        tt.reason_id  AS cs_reason_id,
        #SUM( IF(c.email_body LIKE '%return%', 1, NULL)) as returnCnt,
        IF(c.email_body LIKE '%fit%', 1, NULL) AS fit,
        IF(c.email_body LIKE '%small%', 1, NULL) AS small,
        IF(c.email_body LIKE '%quality%', 1, NULL) AS quality,
        IF(c.email_body LIKE '%material%', 1, NULL) AS material,
        IF(c.email_body LIKE '%size%', 1, NULL) AS size,
        IF(c.email_body LIKE '%large%', 1, NULL) AS large,
        IF(c.email_body LIKE '%exchange%', 1, NULL) AS exchange,
        IF(c.email_body LIKE '%wear%', 1, NULL) AS wear,
        IF(c.email_body LIKE '%big%', 1, NULL) big,
        IF(c.email_body LIKE '%picture%', 1, NULL) AS picture,
         IF(c.email_body LIKE '%broken%', 1, NULL) AS broken,
          IF(c.email_body LIKE '%too%', 1, NULL) AS too,
          IF(c.email_body LIKE '%dress%', 1, NULL) AS dress,
         
          IF(c.email_body LIKE '%track%', 1, NULL) AS ___track,
           IF(c.email_body LIKE '%havent%', 1, NULL) AS havent,
            IF(c.email_body LIKE '%yet%', 1, NULL) AS yet,
             IF(c.email_body LIKE '%month%', 1, NULL) AS month1,
              IF(c.email_body LIKE '%status%', 1, NULL) AS status1,
               IF(c.email_body LIKE '%shipped%', 1, NULL) AS shipped, 
               IF(c.email_body LIKE '%never%', 1, NULL) AS never
               
      FROM ticket t
        LEFT JOIN ticket_content c
          ON c.ticket_id = t.id
        LEFT JOIN ticket_sub_reason r
          ON r.ticket_id = t.id AND r.sub_reason_type = 'original_ticket_sub_reason'
        LEFT JOIN ticket_tag tt
          ON t.id = tt.ticket_id AND tt.tag_id = 47
      WHERE t.reason_id = 5
          AND language_code = 'en'
          AND t.create_time > '2015-01-01' AND t.create_time < '2015-05-10'
      GROUP BY t.id
      ORDER BY c.create_time;