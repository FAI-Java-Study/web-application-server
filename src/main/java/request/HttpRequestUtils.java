package request;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class HttpRequestUtils {
    /**
     * 쿼리 파라미터가 있는 경우 queryString을 인자로 받아 파싱하여 map에 저장합니다.
     * @param queryString
     * @return map
     */
    public static Map<String, String> parseQueryParams(String queryString) {
        Map<String, String> params = new HashMap<>();
        // &와 =로 분리
        String[] tokens = queryString.split("\\&");
        for (String token : tokens) {
            //Key Value로 분리하여 map에 저장
            StringTokenizer st = new StringTokenizer(token, "\\=");
            params.put(st.nextToken(), st.nextToken());
        }

        return params;
    }
}
