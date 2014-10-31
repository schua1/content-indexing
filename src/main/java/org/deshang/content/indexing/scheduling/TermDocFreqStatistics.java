/*
 * Copyright 2014 Deshang group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.deshang.content.indexing.scheduling;

import java.util.HashMap;
import java.util.Map;

public class TermDocFreqStatistics implements Cloneable {

    private class TermDocFreqData implements Cloneable {
        private int docFreq;
        private double docFreqPercent;
        public int getDocFreq() {
            return docFreq;
        }
        public void setDocFreq(int docFreq) {
            this.docFreq = docFreq;
        }
        public double getDocFreqPercent() {
            return docFreqPercent;
        }
        public void setDocFreqPercent(double docFreqPercent) {
            this.docFreqPercent = docFreqPercent;
        }
        @Override
        public TermDocFreqData clone() {
            TermDocFreqData data = new TermDocFreqData();
            data.docFreq = this.docFreq;
            data.docFreqPercent = this.docFreqPercent;
            return data;
        }
    }

    private final String DOC_FREQ_DATA_KEY_TOTAL = "total";
    private final String DOC_FREQ_DATA_KEY_PERSON = "person";

    private Map<String, Map<String, TermDocFreqData>> mapStatistics = new HashMap<String, Map<String, TermDocFreqData>>();
    
    {
        mapStatistics.put(DOC_FREQ_DATA_KEY_TOTAL, new HashMap<String, TermDocFreqData>());
        mapStatistics.put(DOC_FREQ_DATA_KEY_PERSON, new HashMap<String, TermDocFreqData>());
    }
    
    public void putTermTotalDocFreqInfo(String term, int totalDocFreq, double totalDocFreqPercent) {
        Map<String, TermDocFreqData> datas = mapStatistics.get(DOC_FREQ_DATA_KEY_TOTAL);
        if (datas == null) {
            datas = new HashMap<String, TermDocFreqData>();
            mapStatistics.put(DOC_FREQ_DATA_KEY_TOTAL, datas);
        }
        
        TermDocFreqData data = datas.get(term);
        if (data == null) {
            data = new TermDocFreqData();
            datas.put(term, data);
        }
        data.setDocFreq(totalDocFreq);
        data.setDocFreqPercent(totalDocFreqPercent);
    }

    public void putTermPersonDocFreqInfo(String term, int personDocFreq, double personDocFreqPercent) {
        Map<String, TermDocFreqData> datas = mapStatistics.get(DOC_FREQ_DATA_KEY_PERSON);
        if (datas == null) {
            datas = new HashMap<String, TermDocFreqData>();
            mapStatistics.put(DOC_FREQ_DATA_KEY_PERSON, datas);
        }
        
        TermDocFreqData data = datas.get(term);
        if (data == null) {
            data = new TermDocFreqData();
            datas.put(term, data);
        }
        data.setDocFreq(personDocFreq);
        data.setDocFreqPercent(personDocFreqPercent);
    }

    public long getTermTotalDocFreq(String term) {
        Map<String, TermDocFreqData> datas = mapStatistics.get(DOC_FREQ_DATA_KEY_TOTAL);
        if (datas != null) {
            
            TermDocFreqData data = datas.get(term);
            if (data != null) {
                return data.getDocFreq();
            }
            return 0;
        }
        return 0;
    }

    public double getTermTotalDocFreqPercent(String term) {
        Map<String, TermDocFreqData> datas = mapStatistics.get(DOC_FREQ_DATA_KEY_TOTAL);
        if (datas != null) {
            
            TermDocFreqData data = datas.get(term);
            if (data != null) {
                return data.getDocFreqPercent();
            }
            return 0;
        }
        return 0;
    }

    public int getTermPersonDocFreq(String term) {
        Map<String, TermDocFreqData> datas = mapStatistics.get(DOC_FREQ_DATA_KEY_PERSON);
        if (datas != null) {
            
            TermDocFreqData data = datas.get(term);
            if (data != null) {
                return data.getDocFreq();
            }
            return 0;
        }
        return 0;
    }

    public double getTermPersonDocFreqPercent(String term) {
        Map<String, TermDocFreqData> datas = mapStatistics.get(DOC_FREQ_DATA_KEY_PERSON);
        if (datas != null) {
            
            TermDocFreqData data = datas.get(term);
            if (data != null) {
                return data.getDocFreqPercent();
            }
            return 0;
        }
        return 0;
    }

    public String[] getAllTotalTerms() {
        Map<String, TermDocFreqData> datas = mapStatistics.get(DOC_FREQ_DATA_KEY_TOTAL);
        if (datas != null) {
            
            return datas.keySet().toArray(new String[0]);
        }
        return new String[0];
    }

    public String[] getAllPersonTerms() {
        Map<String, TermDocFreqData> datas = mapStatistics.get(DOC_FREQ_DATA_KEY_PERSON);
        if (datas != null) {
            
            return datas.keySet().toArray(new String[0]);
        }
        return new String[0];
    }

    public void switchPersonToTotal() {
        mapStatistics.put(DOC_FREQ_DATA_KEY_TOTAL, mapStatistics.get(DOC_FREQ_DATA_KEY_PERSON));
        mapStatistics.put(DOC_FREQ_DATA_KEY_PERSON, new HashMap<String, TermDocFreqData>());
    }

    @Override
    public TermDocFreqStatistics clone() {
        TermDocFreqStatistics newStatistics = new TermDocFreqStatistics();
        for (Map.Entry<String, Map<String, TermDocFreqData>> datas : mapStatistics.entrySet()) {
            Map<String, TermDocFreqData> newTermDocFreqDatas = new HashMap<String, TermDocFreqData>();
            for (Map.Entry<String, TermDocFreqData> dataEntry : datas.getValue().entrySet()) {
                newTermDocFreqDatas.put(dataEntry.getKey(), dataEntry.getValue().clone());
            }
            newStatistics.mapStatistics.put(datas.getKey(), newTermDocFreqDatas);
        }
        return newStatistics;
    }
}
