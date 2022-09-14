package com.workduo.area.common.service;

import com.workduo.area.sidoarea.entity.SidoArea;
import com.workduo.area.sidoarea.repository.SidoAreaRepository;
import com.workduo.area.siggarea.entity.SiggArea;
import com.workduo.area.siggarea.repository.SiggAreaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AreaService {

    private final SidoAreaRepository sidoAreaRepository;
    private final SiggAreaRepository siggAreaRepository;

    public void insertArea() throws Exception {
        JSONArray features;

        try {
            features = parseData();
        } catch (Exception e) {
            throw e;
        }

        HashSet<String> sidoSet = new HashSet<>();
        List<SidoArea> sidoAreaList = new ArrayList<>();
        HashSet<String> siggSet = new HashSet<>();
        List<SiggArea> siggAreaList = new ArrayList<>();

        for (int i = 0; i < features.size(); i++) {
            JSONObject feature = (JSONObject) features.get(i);
            JSONObject properties = (JSONObject) feature.get("properties");

            SidoArea sidoArea = SidoArea.builder()
                    .sido(properties.get("sido").toString())
                    .sidonm(properties.get("sidonm").toString())
                    .build();

            if (sidoSet.add(properties.get("sido").toString())) {
                sidoAreaList.add(sidoArea);
            }

            if (siggSet.add(properties.get("sgg").toString())) {
                siggAreaList.add(
                        SiggArea.builder()
                                .sgg(properties.get("sgg").toString())
                                .sidoArea(sidoArea)
                                .sidonm(properties.get("sidonm").toString())
                                .sggnm(properties.get("sggnm").toString())
                                .build()
                );
            }
        }

        sidoAreaRepository.saveAll(sidoAreaList);
        siggAreaRepository.saveAll(siggAreaList);
    }

    private JSONArray parseData() throws Exception {
        JSONArray features = null;
        ClassPathResource resource =
                new ClassPathResource("HangJeongDong_ver20220401.geojson");

        try {
            BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(resource.getInputStream()));

            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = bufferedReader.readLine()) != null) {
                response.append(inputLine);
            }

            bufferedReader.close();

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(response.toString());
            features = (JSONArray) jsonObject.get("features");

            if (features == null || features.size() <= 0) {
                throw new Exception("features size zero");
            }

        } catch (Exception e) {
            throw e;
        }

        return features;
    }
}
