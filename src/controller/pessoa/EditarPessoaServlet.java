package controller.pessoa;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import model.FotoPerfil;
import model.Pessoa;
import model.repository.PessoaRepository;

/**
 * Servlet implementation class EditarPessoaServlet
 */
@WebServlet("/pessoa/editar")
@MultipartConfig
public class EditarPessoaServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public EditarPessoaServlet()
	{
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		HttpSession session = request.getSession();
		
		if ("OK".equals(session.getAttribute("usuarioAutenticado")))
		{
			int idPessoa = 0;
			Pessoa p = null;
			String pIdPessoa = request.getParameter("idpessoa");
			
			if (pIdPessoa != null)
			{
				try
				{
					idPessoa = Integer.parseInt(pIdPessoa);
				}
				catch (Exception e)
				{
					System.out.println("ID de Pessoa inv�lido!");
					System.out.println(e.getMessage());
					System.out.println(e.getStackTrace().toString());
					
					for (StackTraceElement ste : e.getStackTrace())
					{
						System.out.println(ste.toString());
					}
				}
			}
			
			if (idPessoa > 0)
			{
				p = PessoaRepository.recuperarPessoaPorId(idPessoa);
			}
			
			request.setAttribute("tituloPagina", "Editar Pessoa");
			request.setAttribute("pathPagina", "/pessoa/editar.jsp");
			request.setAttribute("pessoa", p);
		}
		else
		{
			request.setAttribute("tituloPagina", "Acesso Negado!");
			request.setAttribute("pathPagina", "/unauthorized.jsp");
		}
		
		request.setAttribute("doServidor", true);
		
		RequestDispatcher rd = request.getRequestDispatcher("/template.jsp");
		
		rd.forward(request, response);
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		HttpSession session = request.getSession();
		
		if ("OK".equals(session.getAttribute("usuarioAutenticado")))
		{
			int idPessoa = 0;
			String pIdPessoa = request.getParameter("numIdPessoa");
			
			if (pIdPessoa != null)
			{
				try
				{
					idPessoa = Integer.parseInt(pIdPessoa);
				}
				catch (Exception e)
				{
					System.out.println("ID de Pessoa inv�lido!");
					System.out.println(e.getMessage());
					System.out.println(e.getStackTrace().toString());
					
					for (StackTraceElement ste : e.getStackTrace())
					{
						System.out.println(ste.toString());
					}
				}
			}
			
			if (idPessoa > 0)
			{
				Pessoa p = PessoaRepository.recuperarPessoaPorId(idPessoa);
				
				if (p != null)
				{
					p.setNomePessoa(request.getParameter("txtNome"));
					p.setEnderecoPessoa(request.getParameter("txtEndereco"));
					p.setCepPessoa(Long.parseLong(request.getParameter("numCep")));
					p.setTelefonePessoa(request.getParameter("txtTelefone"));
					p.setEmail(request.getParameter("txtEmail"));
					p.setRendaPessoa(Double.parseDouble(request.getParameter("numRenda").replace(',', '.')));
					p.setSituacaoPessoa("on".equals(request.getParameter("chkAtivo")) ? 1 : 0);
					p.setSenha(request.getParameter("txtSenha"));
					
					// Leitura e processamento do arquivo referente � foto de perfil
					Part filePart = request.getPart("fileFotoPerfil");
					
					if(filePart != null)
					{
						System.out.println("Tentou atualizar foto.");
						
						// Coleta o nome do arquivo submetido
						String fileName = filePart.getSubmittedFileName();
						
						// Coleta o content type do arquivo submetido (ex. "image/jpeg")
						String fileContentType = filePart.getContentType();
						
						// Coleta os bytes referentes ao arquivo
						InputStream fileContent = filePart.getInputStream();
						byte fileBytes[] = new byte[fileContent.available()];
						fileContent.read(fileBytes);
						
						// Cria o objeto e o atribui como atributo da pessoa
						FotoPerfil fotoPerfil = new FotoPerfil(fileName, fileContentType, fileBytes);
						
						p.setFotoPerfil(fotoPerfil);
					}
					
					PessoaRepository.atualizarPessoa(p);
					
					request.setAttribute("mensagemAlerta", "Cadastro atualizado com sucesso!");
				}
			}
			
			Set<Pessoa> pessoas = PessoaRepository.recuperarPessoas();
			
			PessoaRepository.closeEntityManager();
			
			request.setAttribute("pessoasCadastradas", pessoas);
			request.setAttribute("tituloPagina", "Cadastro de Pessoas");
			request.setAttribute("pathPagina", "/pessoa/listar.jsp");
		}
		else
		{
			request.setAttribute("tituloPagina", "Acesso Negado!");
			request.setAttribute("pathPagina", "/unauthorized.jsp");
		}
		
		request.setAttribute("doServidor", true);
		
		RequestDispatcher rd = request.getRequestDispatcher("/template.jsp");
		
		rd.forward(request, response);
	}
}
