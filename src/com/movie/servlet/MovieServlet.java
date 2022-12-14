package com.movie.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.movie.bean.Cinema;
import com.movie.bean.Movie;
import com.movie.service.CinemaService;
import com.movie.service.MovieService;
import com.movie.util.SystemParamsUtils;

@WebServlet("/movie")
public class MovieServlet extends HttpServlet{
	
	private MovieService movieService;
	
	private CinemaService cinemaService;
	
	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException {
		movieService = new MovieService();
		cinemaService = new CinemaService();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String method = req.getParameter("method");
		if("findMovieById".equals(method)){
			findMovieById(req,resp);
		}else if("findAllMovies".equals(method)){
			findAllMovies(req,resp);
		}else if("findAllMoviesBack".equals(method)){
			findAllMoviesBack(req,resp);
		}else if("findMoviesByName".equals(method)){
			findMoviesByName(req,resp);
		}else if("findMoviesByType".equals(method)){
			findMoviesByType(req,resp);
		}else if("sortAllMovies".equals(method)){
			sortAllMovies(req,resp);
		}else if("deleteMovie".equals(method)){
			deleteMovie(req,resp);
		}else{
			try {
				Movie movie = new Movie();
				//ๆๆ่กจๅ้ๆไปถ็ฑปๅ็้ๅ
		        Map<String,Object> param = new HashMap<String,Object>();  
				//ๅๅปบไธไธช่งฃๆๅจๅทฅๅ
		        DiskFileItemFactory factory = new DiskFileItemFactory();
		        //ๆไปถไธไผ?่งฃๆๅจ
		        ServletFileUpload upload = new ServletFileUpload(factory);
				//่งฃๆ่ฏทๆฑ๏ผๅฐ่กจๅไธญๆฏไธช่พๅฅ้กนๅฐ่ฃๆไธไธชFileItemๅฏน่ฑก
		        List<FileItem> fileItems = upload.parseRequest(req);
		        Boolean isFile =false;
		        for (FileItem fileItem : fileItems) {
		        	//ๅคๆญ่พๅฅ็็ฑปๅๆฏ ๆฎ้่พๅฅ้กน ่ฟๆฏๆไปถ
		            if (!fileItem.isFormField()) {
		                //ไธไผ?็ๆฏๆไปถ๏ผ่ทๅพๆไปถไธไผ?ๅญๆฎตไธญ็ๆไปถๅ
		                //ๆณจๆIEๆFireFoxไธญ่ทๅ็ๆไปถๅๆฏไธไธๆ?ท็๏ผIEไธญๆฏ็ปๅฏน่ทฏๅพ๏ผFireFoxไธญๅชๆฏๆไปถๅ
		                String fileName = fileItem.getName();
		                System.out.println(fileName);
		                //Substringๆฏๅญ็ฌฆไธฒๆชๅ๏ผ่ฟๅๅผๆฏไธไธชๆชๅๅ็ๅญ็ฌฆไธฒ
		                //lastIndexOf(".")ๆฏไปๅณๅๅทฆๆฅ,่ทๅ.ไนๅ็ๅญ็ฌฆไธฒ
		                String ext = fileName.substring(fileName.lastIndexOf("."));
		                //UUID.randomUUID().toString()ๆฏjavaJDKๆไพ็ไธไธช่ชๅจ็ๆไธป้ฎ็ๆนๆณ, UUID็ๅฏไธ็ผบ้ทๅจไบ็ๆ็็ปๆไธฒไผๆฏ่พ้ฟ
		                String name = UUID.randomUUID()+ext;
		                //ๅฐFileItemๅฏน่ฑกไธญไฟๅญ็ไธปไฝๅๅฎนไฟๅญๅฐๆไธชๆๅฎ็ๆไปถไธญ
		    			String path =req.getRealPath("/file") + "/upload/movies/" + name;
		                File file = new File(path);           
		                String filePath = "/file/upload/movies/" + name;
		                movie.setMovie_picture(filePath);
		        		
		                fileItem.write(file);
		                isFile = true;
		            }else{
		            	param.put(fileItem.getFieldName(), fileItem.getString("utf-8"));//ๅฆๆไฝ?้กต้ข็ผ็?ๆฏutf-8็
		            }
		        }
		        movie.setMovie_cn_name(param.get("movie_cn_name") == null?"":param.get("movie_cn_name").toString());
				movie.setMovie_fg_name(param.get("movie_fg_name") == null?"":param.get("movie_fg_name").toString());
				movie.setMovie_actor(param.get("movie_actor") == null?"":param.get("movie_actor").toString());
				movie.setMovie_director(param.get("movie_director") == null?"":param.get("movie_director").toString());
				movie.setMovie_detail(param.get("movie_detail") == null?"":param.get("movie_detail").toString());
				movie.setMovie_duration(param.get("movie_duration") == null?"":param.get("movie_duration").toString());
				movie.setMovie_type(param.get("movie_type") == null?"":param.get("movie_type").toString());
				movie.setMovie_country(param.get("movie_country") == null?"":param.get("movie_country").toString());
				String movie_releaseDate = param.get("movie_releaseDate") == null?"":param.get("movie_releaseDate").toString();
				if(!"".equals(movie_releaseDate)){
					Date date = new SimpleDateFormat("yyyy-MM-dd").parse(movie_releaseDate);
					movie.setReleaseDate(date);
					
				}
				resp.setContentType("text/json; charset=utf-8");    // ่ฎพ็ฝฎresponse็็ผ็?ๅๆ?ผๅผ
		        PrintWriter out = resp.getWriter();
		        Map<String,Object> map = new HashMap<>();
		        //ๆฐๅข็ตๅฝฑ
		        if("addMovie".equals(param.get("method").toString())){
		        	movie.setMovie_state(1);
		        	Integer rs = movieService.addMovie(movie);
		    		if(rs > 0) {
		    			map.put("code", 0);
		    			map.put("msg", "ๆทปๅ?ๆๅ~");
		    		}else {
		    			map.put("code", 200);
		    			map.put("msg", "ๆทปๅ?ๅคฑ่ดฅ~");
		    		}
		        }else{//ๆดๆฐ็ตๅฝฑ
		        	movie.setMovie_id(Long.valueOf(param.get("movie_id").toString()));
		        	if(!isFile){
		    			Movie oldMovie = this.movieService.findMovieById(movie.getMovie_id());
		    			movie.setMovie_picture(oldMovie.getMovie_picture());
		    		}
		    		Integer rs = movieService.updateMovie(movie);
		    		if(rs > 0) {
		    			map.put("code", 0);
		    			map.put("msg", "ไฟฎๆนๆๅ~");
		    		}else {
		    			map.put("code", 200);
		    			map.put("msg", "ไฟฎๆนๅคฑ่ดฅ~");
		    		}
		        }
		        String resJSON = JSON.toJSONString(map);
		        out.print(resJSON); // ่พๅบ
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		// ้ๅฎๅ
		//resp.sendRedirect("product_list");
	}
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	
	private void findAllMoviesBack(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		Integer page = req.getParameter("page") == null?1:Integer.valueOf(req.getParameter("page"));
		Integer limit = req.getParameter("limit") == null?10:Integer.valueOf(req.getParameter("limit"));
		List<Movie> list = movieService.findAllMoviesPage(1,page,limit);
		resp.setContentType("text/json; charset=utf-8");    // ่ฎพ็ฝฎresponse็็ผ็?ๅๆ?ผๅผ
        PrintWriter out = resp.getWriter();
        Map<String,Object> map = new HashMap<>();  
        map.put("code", 0);
        map.put("count", movieService.findAllMovies(1).size());
        map.put("data", list);
        String resJSON = JSON.toJSONString(map);
        out.print(resJSON); // ่พๅบ
		
	}
	
	private void findAllMovies(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		List<Movie> list = movieService.findAllMovies(1);
		List<Movie> offList = movieService.sortMovieByScore();
		String type[] = {"ๅๅง","ๅจไฝ","็ฑๆ","ๅจ็ป","็งๅนป","ๆๆ","ๅ้ฉ","็ฏ็ฝช","ๆฌ็"};
		ArrayList<Object> typeArr = new ArrayList<Object>();
		for(int i = 0;i < type.length;i++) {
			List<Movie> movieList = this.movieService.findMoviesLikeType("",type[i],"","","");
			float boxOffice = 0;
			for(int j = 0; j < movieList.size();j++) {
				boxOffice += movieList.get(j).getMovie_boxOffice();
			}
			JSONObject typeJson = new JSONObject();
			typeJson.put(type[i], boxOffice);
			typeArr.add(typeJson);
		}
		
		resp.setContentType("text/json; charset=utf-8");    // ่ฎพ็ฝฎresponse็็ผ็?ๅๆ?ผๅผ
        PrintWriter out = resp.getWriter();
        Map<String,Object> map = new HashMap<>();  
        map.put("code", 0);
        map.put("count", list.size());
        map.put("data", list);
        map.put("sort", offList);
        map.put("type", typeArr);
        String resJSON = JSON.toJSONString(map);
        out.print(resJSON); // ่พๅบ
		
	}
	
	public void findMovieById(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Long movie_id = req.getParameter("movie_id") == null?1:Long.valueOf(req.getParameter("movie_id"));
		Long cinema_id = req.getParameter("cinema_id") == null?1:Long.valueOf(req.getParameter("cinema_id"));
		int date = req.getParameter("date") == null?0:Integer.valueOf(req.getParameter("date").toString());
		int page = req.getParameter("page") == null?1:Integer.valueOf(req.getParameter("page"));
		int limit = req.getParameter("limit") == null?5:Integer.valueOf(req.getParameter("limit"));
		Calendar calendar = Calendar.getInstance(); 
		String dateStr = "";
		if(date == 0){
			int month = calendar.get(Calendar.MONTH)+1;
			int day = calendar.get(Calendar.DATE);
			String monthStr = month < 10 ? "0" + month : month + "";
			String dayStr = day < 10 ? "0" + day : day + "";
			dateStr = calendar.get(Calendar.YEAR)+"-"+monthStr+"-"+dayStr;
		}else if(date == 1){
			calendar.add(Calendar.DATE, 1);
			int month = calendar.get(Calendar.MONTH)+1;
			int day = calendar.get(Calendar.DATE);
			String monthStr = month < 10 ? "0" + month : month + "";
			String dayStr = day < 10 ? "0" + day : day + "";
			dateStr = calendar.get(Calendar.YEAR)+"-"+monthStr+"-"+dayStr;
		}else if(date == 2){
			calendar.add(Calendar.DATE, 2);
			int month = calendar.get(Calendar.MONTH)+1;
			int day = calendar.get(Calendar.DATE);
			String monthStr = month < 10 ? "0" + month : month + "";
			String dayStr = day < 10 ? "0" + day : day + "";
			dateStr = calendar.get(Calendar.YEAR)+"-"+monthStr+"-"+dayStr;
		}
		Movie movie = movieService.findMovieById(movie_id);
		List<Cinema> list = cinemaService.findCinemasByMovieIdPage(movie_id,cinema_id,dateStr,page,limit);
		List<Cinema> allCinemaList = cinemaService.findAllCinemas();
		resp.setContentType("text/json; charset=utf-8");    // ่ฎพ็ฝฎresponse็็ผ็?ๅๆ?ผๅผ
        PrintWriter out = resp.getWriter();
        Map<String,Object> map = new HashMap<>();  
        map.put("code", 0);
        map.put("data", movie);
        map.put("cinemaList",list);
        map.put("cinemaCount",cinemaService.findCinemasByMovieId(movie_id,cinema_id,dateStr).size());
        map.put("allCinemaList",allCinemaList);
        String resJSON = JSON.toJSONString(map);
        out.print(resJSON); // ่พๅบ
	}
	
	public void findMoviesByName(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String name = req.getParameter("name") == null?"":req.getParameter("name").toString();

		List<Movie> list = movieService.findMoviesLikeName(name);
		resp.setContentType("text/json; charset=utf-8");    // ่ฎพ็ฝฎresponse็็ผ็?ๅๆ?ผๅผ
        PrintWriter out = resp.getWriter();
        Map<String,Object> map = new HashMap<>();  
        map.put("code", 0);
        map.put("data", list);
        map.put("count",list.size());
        String resJSON = JSON.toJSONString(map);
        out.print(resJSON); // ่พๅบ
	}
	
	public void findMoviesByType(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Integer page = req.getParameter("page") == null?1:Integer.valueOf(req.getParameter("page"));
		Integer limit = req.getParameter("limit") == null?10:Integer.valueOf(req.getParameter("limit"));
		String order = req.getParameter("order") == null?"":req.getParameter("order").toString();
		String type = req.getParameter("type") == null?"":req.getParameter("type").toString();
		String area = req.getParameter("area") == null?"":req.getParameter("area").toString();
		String year = req.getParameter("year") == null?"":req.getParameter("year").toString();
		String searchMovie = req.getParameter("searchMovie") == null?"":req.getParameter("searchMovie").toString();
		if("0".equals(type) || "ๅจ้จ".equals(type)){
			type = "";
		}
		if("0".equals(area) || "ๅจ้จ".equals(area)){
			area = "";
		}
		if("0".equals(year) || "ๅจ้จ".equals(year)){
			year = "";
		}
		List<Movie> list = movieService.findMoviesLikeTypePage(page,limit,order,type,area,year,searchMovie);
		resp.setContentType("text/json; charset=utf-8");    // ่ฎพ็ฝฎresponse็็ผ็?ๅๆ?ผๅผ
        PrintWriter out = resp.getWriter();
        Map<String,Object> map = new HashMap<>();  
        map.put("code", 0);
        map.put("data", list);
        map.put("count",movieService.findMoviesLikeType(order,type,area,year,searchMovie).size());
        String resJSON = JSON.toJSONString(map);
        out.print(resJSON); // ่พๅบ
	}
	
	public void sortAllMovies(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		String order = req.getParameter("order") == null?"":req.getParameter("order").toString();

		List<Movie> list = new ArrayList<Movie>();
		switch (order) {
			case "็ญ้จ":
				list = movieService.sortMovieByCount();
				break;
			case "ๆถ้ด":
				list = movieService.sortMovieByDate();
				break;
			case "่ฏไปท":
				list = movieService.sortMovieByScore();
				break;
		}
		resp.setContentType("text/json; charset=utf-8");    // ่ฎพ็ฝฎresponse็็ผ็?ๅๆ?ผๅผ
        PrintWriter out = resp.getWriter();
        Map<String,Object> map = new HashMap<>();  
        map.put("code", 0);
        map.put("data", list);
        map.put("count",list.size());
        String resJSON = JSON.toJSONString(map);
        out.print(resJSON); // ่พๅบ
	}
	
	public void deleteMovie(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		Long movie_id = req.getParameter("movie_id") == null?1:Long.valueOf(req.getParameter("movie_id"));

		resp.setContentType("text/json; charset=utf-8");    // ่ฎพ็ฝฎresponse็็ผ็?ๅๆ?ผๅผ
        PrintWriter out = resp.getWriter();
        Map<String,Object> map = new HashMap<>();  
		Integer rs = movieService.deleteMovie(movie_id);
		if(rs > 0) {
			map.put("code",0);
			map.put("msg","ๅ?้คๆๅ~");
		}else {
			map.put("code", 200);
			map.put("msg", "ๅ?้คๅคฑ่ดฅ~");
		}
		
        String resJSON = JSON.toJSONString(map);
        out.print(resJSON); // ่พๅบ
	}
	
	public void addMovie(HttpServletRequest req, HttpServletResponse resp) throws Exception  {

		Movie movie = new Movie();
		
		//ๅๅปบไธไธช่งฃๆๅจๅทฅๅ
        DiskFileItemFactory factory = new DiskFileItemFactory();
        //ๆไปถไธไผ?่งฃๆๅจ
        ServletFileUpload upload = new ServletFileUpload(factory);
		
        //่งฃๆ่ฏทๆฑ๏ผๅฐ่กจๅไธญๆฏไธช่พๅฅ้กนๅฐ่ฃๆไธไธชFileItemๅฏน่ฑก
        List<FileItem> fileItems = upload.parseRequest(req);
        //ๆๆ่กจๅ้ๆไปถ็ฑปๅ็้ๅ
        Map<String,Object> param = new HashMap<String,Object>();  
        // ่ฟญไปฃ่กจๅๆฐๆฎ
        for (FileItem fileItem : fileItems) {
            //ๅคๆญ่พๅฅ็็ฑปๅๆฏ ๆฎ้่พๅฅ้กน ่ฟๆฏๆไปถ
            if (!fileItem.isFormField()) {
                //ไธไผ?็ๆฏๆไปถ๏ผ่ทๅพๆไปถไธไผ?ๅญๆฎตไธญ็ๆไปถๅ
                //ๆณจๆIEๆFireFoxไธญ่ทๅ็ๆไปถๅๆฏไธไธๆ?ท็๏ผIEไธญๆฏ็ปๅฏน่ทฏๅพ๏ผFireFoxไธญๅชๆฏๆไปถๅ
                String fileName = fileItem.getName();
                System.out.println(fileName);
                //Substringๆฏๅญ็ฌฆไธฒๆชๅ๏ผ่ฟๅๅผๆฏไธไธชๆชๅๅ็ๅญ็ฌฆไธฒ
                //lastIndexOf(".")ๆฏไปๅณๅๅทฆๆฅ,่ทๅ.ไนๅ็ๅญ็ฌฆไธฒ
                String ext = fileName.substring(fileName.lastIndexOf("."));
                //UUID.randomUUID().toString()ๆฏjavaJDKๆไพ็ไธไธช่ชๅจ็ๆไธป้ฎ็ๆนๆณ, UUID็ๅฏไธ็ผบ้ทๅจไบ็ๆ็็ปๆไธฒไผๆฏ่พ้ฟ
                String name = UUID.randomUUID()+ext;
                //ๅฐFileItemๅฏน่ฑกไธญไฟๅญ็ไธปไฝๅๅฎนไฟๅญๅฐๆไธชๆๅฎ็ๆไปถไธญ
    			String path = req.getRealPath("/file")+ "/upload/head/" + name;
                File file = new File(path);
            
                String filePath = "/file/upload/head/" + name;
                movie.setMovie_picture(filePath);
        		movie.setMovie_state(1);
                fileItem.write(file);
            }else{
            	param.put(fileItem.getFieldName(), fileItem.getString("utf-8"));//ๅฆๆไฝ?้กต้ข็ผ็?ๆฏutf-8็
            }
        }
        
        movie.setMovie_cn_name(param.get("movie_cn_name") == null?"":param.get("movie_cn_name").toString());
		movie.setMovie_fg_name(param.get("movie_fg_name") == null?"":param.get("movie_fg_name").toString());
		movie.setMovie_actor(param.get("movie_actor") == null?"":param.get("movie_actor").toString());
		movie.setMovie_director(param.get("movie_director") == null?"":param.get("movie_director").toString());
		movie.setMovie_detail(param.get("movie_detail") == null?"":param.get("movie_detail").toString());
		movie.setMovie_duration(param.get("movie_duration") == null?"":param.get("movie_duration").toString());
		movie.setMovie_type(param.get("movie_type") == null?"":param.get("movie_type").toString());
		movie.setMovie_country(param.get("movie_country") == null?"":param.get("movie_country").toString());
		String movie_releaseDate = param.get("movie_releaseDate") == null?"":param.get("movie_releaseDate").toString();
		if(!"".equals(movie_releaseDate)){
			Date date = new SimpleDateFormat("yyyy-MM-dd").parse(movie_releaseDate);
			movie.setReleaseDate(date);
			
		}
        resp.setContentType("text/json; charset=utf-8");    // ่ฎพ็ฝฎresponse็็ผ็?ๅๆ?ผๅผ
        PrintWriter out = resp.getWriter();
        Map<String,Object> map = new HashMap<>();
        Integer rs = movieService.addMovie(movie);
		if(rs > 0) {
			map.put("code", 0);
			map.put("msg", "ๆทปๅ?ๆๅ~");
		}else {
			map.put("code", 200);
			map.put("msg", "ๆทปๅ?ๅคฑ่ดฅ~");
		}
		String resJSON = JSON.toJSONString(map);
        out.print(resJSON); // ่พๅบ
	}
	
	public void updateMovie(HttpServletRequest req, HttpServletResponse resp) throws Exception{
		String movie_cn_name = req.getParameter("movie_cn_name") == null?"":req.getParameter("movie_cn_name").toString();
		String movie_fg_name = req.getParameter("movie_fg_name") == null?"":req.getParameter("movie_fg_name").toString();
		String movie_actor = req.getParameter("movie_actor") == null?"":req.getParameter("movie_actor").toString();
		String movie_director = req.getParameter("movie_director") == null?"":req.getParameter("movie_director").toString();
		String movie_detail = req.getParameter("movie_detail") == null?"":req.getParameter("movie_detail").toString();
		String movie_duration = req.getParameter("movie_duration") == null?"":req.getParameter("movie_duration").toString();
		String movie_type = req.getParameter("movie_type") == null?"":req.getParameter("movie_type").toString();
		String movie_releaseDate = req.getParameter("movie_releaseDate") == null?"":req.getParameter("movie_releaseDate").toString();
		String movie_country = req.getParameter("movie_country") == null?"":req.getParameter("movie_country").toString();
		Long movie_id = req.getParameter("movie_id") == null?1:Long.valueOf(req.getParameter("movie_id"));

		Movie movie = new Movie();
		movie.setMovie_id(movie_id);
		movie.setMovie_cn_name(movie_cn_name);
		movie.setMovie_fg_name(movie_fg_name);
		movie.setMovie_actor(movie_actor);
		movie.setMovie_director(movie_director);
		movie.setMovie_detail(movie_detail);
		movie.setMovie_duration(movie_duration);
		movie.setMovie_type(movie_type);
		movie.setMovie_country(movie_country);
		if(!"".equals(movie_releaseDate)){
			Date date = new SimpleDateFormat("yyyy-MM-dd").parse(movie_releaseDate);
			movie.setReleaseDate(date);
			
		}
		
		Boolean isFile = false;
		//ๅๅปบไธไธช่งฃๆๅจๅทฅๅ
        DiskFileItemFactory factory = new DiskFileItemFactory();
        //ๆไปถไธไผ?่งฃๆๅจ
        ServletFileUpload upload = new ServletFileUpload(factory);
		
        //่งฃๆ่ฏทๆฑ๏ผๅฐ่กจๅไธญๆฏไธช่พๅฅ้กนๅฐ่ฃๆไธไธชFileItemๅฏน่ฑก
        List<FileItem> fileItems = upload.parseRequest(req);
        // ่ฟญไปฃ่กจๅๆฐๆฎ
        for (FileItem fileItem : fileItems) {
            //ๅคๆญ่พๅฅ็็ฑปๅๆฏ ๆฎ้่พๅฅ้กน ่ฟๆฏๆไปถ
            if (!fileItem.isFormField()) {
                //ไธไผ?็ๆฏๆไปถ๏ผ่ทๅพๆไปถไธไผ?ๅญๆฎตไธญ็ๆไปถๅ
                //ๆณจๆIEๆFireFoxไธญ่ทๅ็ๆไปถๅๆฏไธไธๆ?ท็๏ผIEไธญๆฏ็ปๅฏน่ทฏๅพ๏ผFireFoxไธญๅชๆฏๆไปถๅ
                String fileName = fileItem.getName();
                System.out.println(fileName);
                //Substringๆฏๅญ็ฌฆไธฒๆชๅ๏ผ่ฟๅๅผๆฏไธไธชๆชๅๅ็ๅญ็ฌฆไธฒ
                //lastIndexOf(".")ๆฏไปๅณๅๅทฆๆฅ,่ทๅ.ไนๅ็ๅญ็ฌฆไธฒ
                String ext = fileName.substring(fileName.lastIndexOf("."));
                //UUID.randomUUID().toString()ๆฏjavaJDKๆไพ็ไธไธช่ชๅจ็ๆไธป้ฎ็ๆนๆณ, UUID็ๅฏไธ็ผบ้ทๅจไบ็ๆ็็ปๆไธฒไผๆฏ่พ้ฟ
                String name = UUID.randomUUID()+ext;
                //ๅฐFileItemๅฏน่ฑกไธญไฟๅญ็ไธปไฝๅๅฎนไฟๅญๅฐๆไธชๆๅฎ็ๆไปถไธญ
    			String path = req.getRealPath("/file")+ "/upload/head/" + name;
                File file = new File(path);
            
                String filePath = "/file/upload/head/" + name;
                movie.setMovie_picture(filePath);
                fileItem.write(file);
                isFile = true;
            }
        }
        
		if(!isFile){
			Movie oldMovie = this.movieService.findMovieById(movie.getMovie_id());
			movie.setMovie_picture(oldMovie.getMovie_picture());
		}
		
		resp.setContentType("text/json; charset=utf-8");    // ่ฎพ็ฝฎresponse็็ผ็?ๅๆ?ผๅผ
        PrintWriter out = resp.getWriter();
        Map<String,Object> map = new HashMap<>();
		Integer rs = movieService.updateMovie(movie);
		if(rs > 0) {
			map.put("code", 0);
			map.put("msg", "ไฟฎๆนๆๅ~");
		}else {
			map.put("code", 200);
			map.put("msg", "ไฟฎๆนๅคฑ่ดฅ~");
		}
		String resJSON = JSON.toJSONString(map);
        out.print(resJSON); // ่พๅบ
	}
	
}
