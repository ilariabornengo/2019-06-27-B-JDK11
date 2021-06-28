package it.polito.tdp.crimes.model;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	EventsDao dao;
	List<String> vertici;
	Graph<String,DefaultWeightedEdge> grafo;
	List<String> listaBest;
	
	public Model()
	{
		this.dao=new EventsDao();
	}
	
	public void creaGrafo(Integer mese,String categoria)
	{
		this.vertici=new ArrayList<String>();
		this.grafo=new SimpleWeightedGraph<String,DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		//aggiungo i vertici
		this.dao.getVertici(vertici, mese, categoria);
		Graphs.addAllVertices(this.grafo, this.vertici);
		
		//aggiungo gli archi
		for(Adiacenza a:this.dao.getAdiacenze(vertici, mese, categoria))
		{
			if(this.grafo.vertexSet().contains(a.getS1()) && this.grafo.vertexSet().contains(a.getS2()))
			{
				Graphs.addEdge(this.grafo, a.getS1(), a.getS2(), a.getPeso());
			}
		}
	}
	public double pesoMedio()
	{
		double pesoMedio=0;
		double aggiunta=0;
		for(DefaultWeightedEdge d:this.grafo.edgeSet())
		{
			double pesoA=this.grafo.getEdgeWeight(d);
			aggiunta+=pesoA;
		}
		pesoMedio=aggiunta/this.grafo.edgeSet().size();
		return pesoMedio;
	}
	public List<Adiacenza> getAdi(Integer mese, String categoria)
	{
		return this.dao.getAdiacenze(vertici, mese, categoria);
	}
	public List<Adiacenza> getMeglioDi(double pesoM,Integer mese,String categoria)
	{
		List<Adiacenza> OK=new ArrayList<Adiacenza>();
		for(Adiacenza a:this.dao.getAdiacenze(vertici, mese, categoria))
		{
			if(a.peso>pesoM)
			{
				OK.add(a);
			}
		}
		return OK;
	}
	
	public List<String> getListaBest(Adiacenza a)
	{
		String partenza=a.getS1();
		String arrivo=a.getS2();
		this.listaBest=new ArrayList<String>();
		List<String> parziale=new ArrayList<String>();
		parziale.add(partenza);
		ricorsione(parziale,arrivo);
		return this.listaBest;
	}
	private void ricorsione(List<String> parziale, String arrivo) {
			String ultimo=parziale.get(parziale.size()-1);
			if(ultimo.equals(arrivo))
			{
				if(parziale.size()>this.listaBest.size())
				{
					this.listaBest=new ArrayList<String>(parziale);
					return;
				}
			}
			
			//fuori dal caso terminale
			for(String s:Graphs.neighborListOf(this.grafo, ultimo))
			{
				if(!parziale.contains(s))
				{
					parziale.add(s);
					ricorsione(parziale,arrivo);
					parziale.remove(s);
				}
			}
		
	}

	public int getArchi()
	{
		return this.grafo.edgeSet().size();
	}
	public int getVertici()
	{
		return this.grafo.vertexSet().size();
	}
	
	public List<String> categorie()
	{
		return this.dao.getCategoria();
	}
	public List<Integer> mesi()
	{
		return this.dao.getMese();
	}
}
