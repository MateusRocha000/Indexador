package src.indice.estrutura;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;




public class IndiceLight extends Indice  
{
	
	/**
	 * Versao - para gravação do arquivo binário
	 */
	private static final long serialVersionUID = 1L;


	private Map<String,PosicaoVetor> posicaoIndice;
	private int[] arrDocId;
	private int[] arrTermId;
	private int[] arrFreqTermo;
	private int checkFull = 0;
        int initCapGlobal;
	
	/**
	 * Ultimo indice (com algum valor valido) nos vetores
	 */
	private int lastIdx = 0;
	/**
	 * Armazena o ultimo id de termo criado. Utilizado para criar um 
	 * id incremental dos termos.
	 */
	private int lastTermId = 0;
	
		
	public IndiceLight(int initCap)
	{
                initCapGlobal = initCap;
		arrDocId = new int[initCap];
		arrTermId = new int[initCap];
		arrFreqTermo = new int[initCap];
		posicaoIndice = new HashMap<String,PosicaoVetor>();
	}
	
	@Override
	public int getNumDocumentos()
	{
            return posicaoIndice.size();
	}

	/**
	 * Indexa um terminado termo que ocorreu freqTermo vezes em um determinado documento docId.
	 * Armazene o novo termo na última posição do vetor (usando o atributo lastIdx). 
	 * Utilize o posicaoIndice para resgatar o id do termo. 
	 * Caso este id não exista, crie-o utilizando a variável lastTermId. 
	 * Caso o vetor já esteja no seu limite, você deve criar um vetor 10% maior e realocar todos os elementos.
	 * 
	 * **Sobre o Map posicaoIndice***
	 * Você irá usar o Map posicaoIndice agora apenas para definir/resgatar o id deste termo passado como parametro.
	 * Não se preocupe em definir a posicao inicial no vetor (posInicial) nem o número de documentos 
	 * que este termo ocorreu (numOcorrencias). Estes dois atributos (posInicial e numOcorrencias) só serão 
	 * setados ao concluir a indexação (i.e. no método concluiIndexacao), pois, ao concluir, 
	 * o vetor será devidamente ordenado.
	 */
	@Override
	public void index(String termo,int docId,int freqTermo)
	{
            PosicaoVetor posiVetor = posicaoIndice.get(termo);
            
            if(posiVetor != null){
                posiVetor.setNumDocumentos(freqTermo);
                posiVetor.setPosInicial(lastTermId);
                posicaoIndice.put(termo, posiVetor);
            } else {
                PosicaoVetor newPosiVetor = new PosicaoVetor(docId);
                newPosiVetor.setNumDocumentos(freqTermo);
                newPosiVetor.setPosInicial(lastTermId);
                posicaoIndice.put(termo, newPosiVetor);
            }
            
            if(checkFull == initCapGlobal)
            {
                arrTermId = aumentaCapacidadeVetor(arrTermId, 10);
                arrDocId = aumentaCapacidadeVetor(arrDocId, 10);
                arrFreqTermo = aumentaCapacidadeVetor(arrFreqTermo, 10);
                System.gc();
            }  

            arrTermId[lastIdx] = lastTermId;
            arrDocId[lastIdx] = docId;
            arrFreqTermo[lastIdx] = freqTermo;
            
            checkFull++;
            lastIdx++;
            lastTermId++;
	}

        public int[] aumentaCapacidadeVetor(int[] vetor, double d)
        {
            double size = vetor.length;
            double new_size = size * (100+d)/100;
            int new_vet[] = new int[(int)new_size];
            initCapGlobal = (int) new_size;
            for(int i = 0; i < size; i++)
            {
                new_vet[i] = vetor[i];
            }
            
            return new_vet;
        }
	
	@Override
	public Map<String,Integer> getNumDocPerTerm()
        {
           Map<String,Integer>  numDocPerTerm = new HashMap<String, Integer>();
           
           
            for (String termo : posicaoIndice.keySet()) {
                PosicaoVetor posV = posicaoIndice.get(termo);
                numDocPerTerm.put(termo, posV.getNumDocumentos());
            }
            return numDocPerTerm;
	}
        
        
	
	@Override
	public Set<String> getListTermos()
	{
            Set<String> setTermos = new HashSet<String>();
            for (String termo : posicaoIndice.keySet()) {
                setTermos.add(termo);
            }
            return setTermos;
	}
	
	@Override
	public List<Ocorrencia> getListOccur(String termo)
	{
            List<Ocorrencia> listOc = new ArrayList<Ocorrencia>();
            PosicaoVetor posiVetor = posicaoIndice.get(termo);
            int posiInicial = posiVetor.getPosInicial();
            int numeroDocumentos = posiVetor.getNumDocumentos();
           
            for(int i = posiInicial; i <= posiInicial+ numeroDocumentos; i++){
                Ocorrencia oc = new Ocorrencia(
                    arrDocId[i],
                    arrFreqTermo[i]               
                );
                listOc.add(oc);
            }

            return listOc;
	}
	
	/**
	 * Ao concluir a indexação, deve-se ordenar o indice de acordo com o id do termo.
	 * Logo após, atualize a posicaoInicial e numOcorrencia de cada
	 * termo no Map posicaoIndice. 
	 * 
	 * Dica: ao percorrer os vetores, para saber qual instancia PosicaoVetor um id de termo se refere, 
	 * crie um vetor que relaciona o id do termo (como indice) e a instancia PosicaoVetor que esta no mapa posicaoIndice. 
	 * Percorra o mapa posicaoIndice para obter essa relação. 
	 * Ou seja, cosidere que o arrTermoPorId é o vetor criado. Este vetor 
	 * possuirá o tamanho lastTermId+1 (pois o id do termo é incremental) você povoará o este vetor da seguinte forma:
	 * para cada termo pertencente em posicaoIndice: arrTermoPorId[posicaoIndice.get(termo).getIdTermo()] = posicaoIndice.get(termo);
	 * 
	 */
	@Override
	public void concluiIndexacao()
        {
            ordenaIndice(); 
            PosicaoVetor[] posiVetor = new PosicaoVetor[lastIdx + 1];
            int countNumeroDocumentos = 0;
            int posiInicial = 0;
            for (String termo : posicaoIndice.keySet()){
                posiVetor[posicaoIndice.get(termo).getIdTermo()-1] = posicaoIndice.get(termo);
            }
            
            for(int i = 0 ; i< lastIdx; i++){
                if(arrTermId[i] == arrTermId[i+1]){
                    countNumeroDocumentos++;
                }else {
                        posiVetor[arrTermId[i]].setNumDocumentos(countNumeroDocumentos);
                        posiVetor[arrTermId[i]].setPosInicial(posiInicial);
                        posiInicial = i+1;
                 
                 
                    //Quando chegar na última posição pode dar pau. Verificar
                }     
            }
            
            for (String termo : posicaoIndice.keySet()){
               posicaoIndice.put(termo,  posiVetor[posicaoIndice.get(termo).getIdTermo()] );
            }
            
	}

	public void ordenaIndice()
	{
            quickSort(0, lastIdx);
            //insertionSort();
	}

	/**
	 * Algoritmo qucksort baseado em Cormen et. al, Introduction to Algorithms 
	 * e adaptado para utilizar a partição com o pivot aleatório
	 * @param p
	 * @param r
	 */
	private void quickSort(int p, int r){
		if(p<r){
			//System.out.println("p: "+p+" r: "+r);
			int q = partition(p, r);
			quickSort(p,q-1);
			quickSort(q+1, r);
		}
	}
	private int partition(int p,int r){
		//partição com pivot aleatório
		int pivot = (int)(p+Math.random()*(r-p));
		exchange(r,pivot);
		
		int i = p-1;
		for(int j = p; j<=r-1; j++){
			if(compare(j,r)<=0){
				i = i+1;
				exchange(i,j);
			}
		}
		exchange(i+1,r);
		return i+1;
	}
	
	/**
	 * Usando os vetores do indice, 
	 * Retorna >0 se posI>posJ
	 * 		   <0 se posI<posJ
	 * 			0, caso contrário
	 * @param posI
	 * @param posJ
	 * @return
	 */
	public int compare(int posI, int posJ){
		//ordena primeirmente pelo termId
		if(this.arrTermId[posI]!=this.arrTermId[posJ]){
			return this.arrTermId[posI]-this.arrTermId[posJ];
		}else{
			return this.arrDocId[posI]-this.arrDocId[posJ];
		}
	}
	/**
	 * Troca a posição dos vetores
	 * @param posI
	 * @param posJ
	 */
	public void exchange(int posI,int posJ){
		int docAux = this.arrDocId[posI];
		int freqAux = this.arrFreqTermo[posI];
		int termAux = this.arrTermId[posI];
		
		this.arrDocId[posI] = this.arrDocId[posJ];
		this.arrFreqTermo[posI] = this.arrFreqTermo[posJ];
		this.arrTermId[posI] = this.arrTermId[posJ];
		
		this.arrDocId[posJ] = docAux;
		this.arrFreqTermo[posJ] = freqAux;
		this.arrTermId[posJ] = termAux;
		
	}
	
	
	public void setArrs(int[] arrDocId,int[] arrTermId,int[] arrFreqTermo){
		this.arrDocId = Arrays.copyOf(arrDocId, arrDocId.length);
		this.arrTermId = Arrays.copyOf(arrTermId, arrTermId.length);
		this.arrFreqTermo = Arrays.copyOf(arrFreqTermo, arrFreqTermo.length);
		lastIdx = arrFreqTermo.length-1;
		
	}
	public int[] getArrDocId(){
		return this.arrDocId;
	}
	public int[] getArrTermId(){
		return this.arrTermId;
	}
	public int[] getArrFreq(){
		return this.arrFreqTermo;
	}


}
