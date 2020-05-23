package br.com.codenation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


import br.com.codenation.desafio.annotation.Desafio;
import br.com.codenation.desafio.app.MeuTimeInterface;
import br.com.codenation.desafio.exceptions.CapitaoNaoInformadoException;
import br.com.codenation.desafio.exceptions.IdentificadorUtilizadoException;
import br.com.codenation.desafio.exceptions.JogadorNaoEncontradoException;
import br.com.codenation.desafio.exceptions.TimeNaoEncontradoException;


public class DesafioMeuTimeApplication implements MeuTimeInterface {

	Map<Long, Time> times = new TreeMap<>();
	Map<Long, Jogador> jogadores = new TreeMap<>();


	@Desafio("incluirTime")
	public void incluirTime(Long id, String nome, LocalDate dataCriacao, String corUniformePrincipal,
							String corUniformeSecundario) {

		if (times.containsKey(id))
			throw new IdentificadorUtilizadoException();

		times.put(id, new Time(nome, dataCriacao, corUniformePrincipal, corUniformeSecundario));
	}

	@Desafio("incluirJogador")
	public void incluirJogador(Long id, Long idTime, String nome, LocalDate dataNascimento,
							   Integer nivelHabilidade, BigDecimal salario) {

		if (!times.containsKey(idTime))
			throw new TimeNaoEncontradoException();
		if (jogadores.containsKey(id))
			throw new IdentificadorUtilizadoException();
		if (id<0 || idTime<0 || nivelHabilidade < 0 || nivelHabilidade > 100)
			throw new IllegalArgumentException();

		jogadores.put(id, new Jogador(idTime, nome, dataNascimento, nivelHabilidade,salario));
	}

	@Desafio("definirCapitao")
	public void definirCapitao(Long idJogador) {
		if (!jogadores.containsKey(idJogador))
			throw new JogadorNaoEncontradoException();

		Jogador jogadorCapitao = jogadores.get(idJogador);

		jogadores.entrySet()
				.stream()
				.filter(j-> jogadorCapitao.getIdTime().equals(j.getValue()
						.getIdTime()) && j.getValue().getIsCapitao())
				.forEach(j->j.getValue().setIsCapitao(false));

		jogadorCapitao.setIsCapitao(true);
		jogadores.put(idJogador, jogadorCapitao);

	}

	@Desafio("buscarCapitaoDoTime")
	public Long buscarCapitaoDoTime(Long idTime) {

		if (!times.containsKey(idTime))
			throw new TimeNaoEncontradoException();

		Optional<Long> idJogador = jogadores.entrySet()
				.stream()
				.filter(j->idTime.equals(j.getValue().getIdTime()) &&
						j.getValue().getIsCapitao())
				.map(Map.Entry::getKey)
				.findFirst();

		if (!idJogador.isPresent())
			throw new CapitaoNaoInformadoException();

		return idJogador.get();
	}

	@Desafio("buscarNomeJogador")
	public String buscarNomeJogador(Long idJogador) {
		if (!jogadores.containsKey(idJogador))
			throw new JogadorNaoEncontradoException();

		return jogadores.get(idJogador).getNome();
	}

	@Desafio("buscarNomeTime")
	public String buscarNomeTime(Long idTime) {
		if (!times.containsKey(idTime))
			throw new TimeNaoEncontradoException();

		return times.get(idTime).getNome();
	}

	@Desafio("buscarJogadoresDoTime")
	public List<Long> buscarJogadoresDoTime(Long idTime) {

		if (!times.containsKey(idTime))
			throw new TimeNaoEncontradoException();

		return jogadores.entrySet()
				.stream()
				.filter(j->idTime.equals(j.getValue().getIdTime()))
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());
	}

	@Desafio("buscarMelhorJogadorDoTime")
	public Long buscarMelhorJogadorDoTime(Long idTime) {

		if (!times.containsKey(idTime))
			throw new TimeNaoEncontradoException();

		Optional<Long> idMelhorJog = jogadores.entrySet()
				.stream()
				.filter(j->idTime.equals(j.getValue().getIdTime()))
				.max (Comparator.comparingInt(j->j.getValue().getNivelHabilidade()))
				.map (Map.Entry::getKey);

		if (!idMelhorJog.isPresent())
			throw new JogadorNaoEncontradoException();

		return idMelhorJog.get();

	}

	@Desafio("buscarJogadorMaisVelho")
	public Long buscarJogadorMaisVelho(Long idTime) {

		if (!times.containsKey(idTime))
			throw new TimeNaoEncontradoException();

		Optional<Long> idJogMaisVelho = jogadores.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByKey())
				.filter(j->idTime.equals(j.getValue().getIdTime()))
				.min(Comparator.comparing(j->j.getValue().getDataNascimento()))
				.map(Map.Entry::getKey);
		if (!idJogMaisVelho.isPresent())
			throw new JogadorNaoEncontradoException();

		return idJogMaisVelho.get();
	}

	@Desafio("buscarTimes")
	public List<Long> buscarTimes() {

	    return times.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByKey())
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());
	}

	@Desafio("buscarJogadorMaiorSalario")
	public Long buscarJogadorMaiorSalario(Long idTime) {

		if (!times.containsKey(idTime))
			throw new TimeNaoEncontradoException();

		Optional<Long> idJogSalario = jogadores.entrySet()
				.stream()
				.filter(j->idTime.equals(j.getValue().getIdTime()))
				.max(Comparator.comparing(j->j.getValue().getSalario()))
				.map(Map.Entry::getKey);

		if (!idJogSalario.isPresent())
			throw new JogadorNaoEncontradoException();

		return idJogSalario.get();
	}

	@Desafio("buscarSalarioDoJogador")
	public BigDecimal buscarSalarioDoJogador(Long idJogador) {
		if (!jogadores.containsKey(idJogador))
			throw new JogadorNaoEncontradoException();

		return jogadores.get(idJogador).getSalario();
	}

	@Desafio("buscarTopJogadores")
	public List<Long> buscarTopJogadores(Integer top) {

	if (jogadores.isEmpty())
	    new ArrayList<>();

	return jogadores.entrySet()
			.stream()
			.sorted(Map.Entry.comparingByValue(Comparator.comparing(Jogador::getNivelHabilidade).reversed()))
			.map(Map.Entry::getKey)
			.limit(top)
			.collect(Collectors.toList());
	}

	@Desafio("buscarCorCamisaTimeDeFora")
	public String buscarCorCamisaTimeDeFora(Long timeDaCasa, Long timeDeFora) {

		if (!times.containsKey(timeDaCasa) || !times.containsKey(timeDeFora))
			throw new TimeNaoEncontradoException();

		String camisaTimeCasa = times.get(timeDaCasa).getCorUniformePrincipal();
		String camisaTimeFora = times.get(timeDeFora).getCorUniformePrincipal();

		if (camisaTimeCasa.equals(camisaTimeFora))
			return times.get(timeDeFora).getCorUniformeSecundario();

		return times.get(timeDeFora).getCorUniformePrincipal();
	}

}

