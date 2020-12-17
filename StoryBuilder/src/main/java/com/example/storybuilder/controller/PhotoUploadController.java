package com.example.storybuilder.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mortennobel.imagescaling.AdvancedResizeOp;
import com.mortennobel.imagescaling.ResampleOp;

@RestController
public class PhotoUploadController {
	private static final String uploadingDir = "home/boards/uploads/";
	
	@RequestMapping(value="/media/{file}", method=RequestMethod.GET)
	public void getImage(HttpServletResponse response, @PathVariable(value="file") String file) throws Exception {
		File f = new File(uploadingDir+file);
		FileInputStream input = new FileInputStream(f);
		response.setContentType("image/jpg");
		pipeStream(input, response.getOutputStream());
	}

	@RequestMapping(value="/list", method=RequestMethod.GET)
	public List<UploadedFile> getList() {
		File f = new File(uploadingDir);
		String[] files = f.list();
		List<UploadedFile> list = new LinkedList<>();
		for (String file: files) {
			if (!file.startsWith("th-")) {
				list.add(new UploadedFile("/media/"+file, "/media/th-"+file));
			}
		}
		return list;
	}
	
	@RequestMapping(value="/upload", method=RequestMethod.POST)
	public UploadedFile uploadPhotos(@RequestParam("files") MultipartFile[] files) throws Exception {
		String name = null, thumb = null;
		File f = new File(uploadingDir);
		if (!f.exists()) {
			f.mkdirs();
		}
		for (MultipartFile file: files) {
			f = new File(uploadingDir + file.getOriginalFilename());
			FileOutputStream outStream = new FileOutputStream(f);
			pipeStream(file.getInputStream(), outStream);
			outStream.close();
			//file.transferTo(f);
			if (file.getOriginalFilename().toLowerCase().endsWith(".jpg")) {
				proccessPhoto(uploadingDir, file.getOriginalFilename(), f);
			}
			name = "/media/"+file.getOriginalFilename();
			thumb = "/media/th-"+file.getOriginalFilename();
		}
		return new UploadedFile(name, thumb);
	}	
	
	private void pipeStream(InputStream input, OutputStream output) throws IOException {
		byte[] buffer = new byte[8192];
		while (true) {
			int len = input.read(buffer);
			if (len == -1) {
				break;
			}
			output.write(buffer, 0, len);
		}
	}
	
	
	private void proccessPhoto(String dir, String file, File f) throws Exception {
		scaleImage(f, new File(dir, file), 1080, "jpg");
		scaleImage(f, new File(dir, "th-"+file), 300, "jpg");
	}
	
	private void scaleImage(File inputFile, File outputFile, int photoHeight, String ext) throws Exception {
		BufferedImage src = ImageIO.read(inputFile);
		while (src.getHeight() == 0 || src.getWidth() == 0) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				
			}
		}
		if (src.getHeight() <= photoHeight) {
			return;
		}
		float scale = (float) photoHeight / (float) src.getHeight();
		ResampleOp resampleOp = new ResampleOp((int) (scale * src.getWidth()), photoHeight);
		resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.VerySharp);
		BufferedImage rescaled = resampleOp.filter(src, null);
		ImageIO.write(rescaled, ext, outputFile);
	}	
}

class UploadedFile {
	public String file;
	public String thumbnail;
	
	public UploadedFile(String file, String thumbnail) {
		this.file = file;
		this.thumbnail = thumbnail;
	}	
}

