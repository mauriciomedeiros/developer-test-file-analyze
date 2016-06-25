package br.com.segware;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;


public class AnalisadorRelatorioImpl implements IAnalisadorRelatorio{
	private File file;
	private Scanner reader;

	public AnalisadorRelatorioImpl() throws FileNotFoundException {
		file = new File("src/test/java/br/com/segware/relatorio.csv");
		reader = new Scanner(file);
	}

	@Override
	public Map<String, Integer> getTotalEventosCliente() {
		Map<String, Integer> totalCustomerEvents = new HashMap<>();
		String line = "";
		int colunIdCustomer = 1;
		while (reader.hasNext()) {
			line = reader.nextLine();
			String[] coluns = line.split(",");
			Integer totalAdded = totalCustomerEvents.get(coluns[colunIdCustomer]);
			Integer totalEventsByCustomer = totalAdded != null ? totalAdded + 1 : 1;
			totalCustomerEvents.put(coluns[colunIdCustomer], totalEventsByCustomer);
		}
		return totalCustomerEvents;
	}

	@Override
	public Map<String, Long> getTempoMedioAtendimentoAtendente() {
		Map<String, Long> averageTimeForAttendance = new HashMap<>();
		Map<String, Integer> countTotalAttendanceByAttendent = new HashMap<>();
		String line = "";
		int colunIdAttendant = 6;
		int colunDateStart = 4;
		int colunDateEnd = 5;
		while (reader.hasNext()) {
			line = reader.nextLine();
			String[] coluns = line.split(",");
			Long timeAttendance = CalculadoraUtil.calculateTimeAttendance(coluns[colunDateStart], coluns[colunDateEnd]);

			Long allTimeToAttendance = averageTimeForAttendance.get(coluns[colunIdAttendant]);

			if (allTimeToAttendance == null)
				allTimeToAttendance = timeAttendance;
			else
				allTimeToAttendance = allTimeToAttendance + timeAttendance;

			Integer countTotalAttendance = countTotalAttendanceByAttendent.get(coluns[colunIdAttendant]);
			Integer total = countTotalAttendance != null ? countTotalAttendance + 1 : 1;
			countTotalAttendanceByAttendent.put(coluns[colunIdAttendant], total);

			averageTimeForAttendance.put(coluns[colunIdAttendant], allTimeToAttendance);
		}

		Map<String, Long> result = new HashMap<>();
		for (String attendant : averageTimeForAttendance.keySet()) {
			for (String res : countTotalAttendanceByAttendent.keySet()) {
				if (attendant.equals(res)) {
					Long vltotal = averageTimeForAttendance.get(attendant)
							/ countTotalAttendanceByAttendent.get(attendant);
					result.put(attendant, vltotal);
				}
			}
		}
		return result;
	}

	@Override
	public List<Tipo> getTiposOrdenadosNumerosEventosDecrescente() {
		List<Tipo> typesOrderDesc = new ArrayList<>();
		Map<Tipo, Integer> quantityByType = new HashMap<>();
		String line = "";
		int colunType = 3;
		while (reader.hasNext()) {
			line = reader.nextLine();
			String[] coluns = line.split(",");
			Integer quantity = quantityByType.get(Tipo.getValue(coluns[colunType]));
			Integer totalQuantity = quantity != null ? quantity + 1 : 1;
			quantityByType.put(Tipo.getValue(coluns[colunType]), totalQuantity);
		}
		List<Entry<Tipo, Integer>> aux = entriesSortedByValues(quantityByType);
		for (Entry<Tipo, Integer> entry : aux) {
			typesOrderDesc.add(entry.getKey());
		}
		return typesOrderDesc;
	}
	
	private <K, V extends Comparable<? super V>> List<Entry<K, V>> entriesSortedByValues(Map<K, V> map) {
		List<Entry<K, V>> sortedEntries = new ArrayList<Entry<K, V>>(map.entrySet());
		Collections.sort(sortedEntries, new Comparator<Entry<K, V>>() {
			@Override
			public int compare(Entry<K, V> e1, Entry<K, V> e2) {
				return e2.getValue().compareTo(e1.getValue());
			}
		});
		return sortedEntries;
	}

	@Override
	public List<Integer> getCodigoSequencialEventosDesarmeAposAlarme() {
		List<Integer> IdsEventDisarmAfterAlarm = new ArrayList<>();
		String line = "";
		int colunType = 3;
		int colunDateStart = 4;
		int colunId = 0;
		String datePrevious = "";
		while (reader.hasNext()) {
			line = reader.nextLine();
			String[] coluns = line.split(",");
			if (Tipo.getValue(coluns[colunType]) == Tipo.ALARME) {
				datePrevious = coluns[colunDateStart];
				continue;
			}
			if (Tipo.getValue(coluns[colunType]) == Tipo.DESARME
					&& CalculadoraUtil.isPreviousTimeAlarm(datePrevious, coluns[colunDateStart])) {
				IdsEventDisarmAfterAlarm.add(new Integer(coluns[colunId]));
				break;
			}
		}
		return IdsEventDisarmAfterAlarm;
	}
}
