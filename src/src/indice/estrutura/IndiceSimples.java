package src.indice.estrutura;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;




public class IndiceSimples extends Indice
{
	
	
	/**
	 * Versao - para gravação do arquivo binário
	 */
	private static final long serialVersionUID = 1L;
	
	private Map<String,List<Ocorrencia>> mapIndice = new HashMap<String,List<Ocorrencia>>();
	public IndiceSimples()
	{
           
	}
	


	@Override
	public void index(String termo,int docId,int freqTermo) 
	{
            List<Ocorrencia> map = null;

            map = mapIndice.get(termo);
                    
            if (map != null) {
               Ocorrencia oc = new Ocorrencia(docId, freqTermo);
               map.add(oc);
               mapIndice.put(termo, map);
            } else {
               Ocorrencia doTermo = new Ocorrencia(docId, freqTermo);
               List<Ocorrencia> list = new ArrayList<Ocorrencia>();
               list.add(doTermo);
               mapIndice.put(termo,list);
            } 
            
	}

	@Override
	public Map<String,Integer> getNumDocPerTerm()
        {
           Map<String,Integer>  numDocPerTerm = new HashMap<String, Integer>();
        
            for (String termo : mapIndice.keySet()) {
                List<Ocorrencia> list =  mapIndice.get(termo);
                numDocPerTerm.put(termo, list.size());
            }
            return numDocPerTerm;
	}
	
	@Override
	public int getNumDocumentos()
	{
            return mapIndice.size();
	}
	
	@Override
	public Set<String> getListTermos()
	{
            Set<String> setTermos = new HashSet<String>();
            for (String termo : mapIndice.keySet()) {
                setTermos.add(termo);
            }
            return setTermos;
	}	
	
	@Override
	public List<Ocorrencia> getListOccur(String termo)
	{
             return mapIndice.get(termo);
            
	}	
	



}
