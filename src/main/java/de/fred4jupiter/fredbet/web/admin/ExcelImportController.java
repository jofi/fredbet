package de.fred4jupiter.fredbet.web.admin;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.fred4jupiter.fredbet.service.excel.ExcelImportService;
import de.fred4jupiter.fredbet.service.excel.ExcelReadingException;
import de.fred4jupiter.fredbet.web.MessageUtil;

@Controller
@RequestMapping("/excelimport")
public class ExcelImportController {

	private static final Logger LOG = LoggerFactory.getLogger(ExcelImportController.class);

	private static final String REDIRECT_SHOW_PAGE = "redirect:/excelimport/show";

	@Autowired
	private ExcelImportService excelImportService;

	@Autowired
	private MessageUtil messageUtil;
	
	@ModelAttribute("excelUploadCommand")
	public ExcelUploadCommand initExcelUploadCommand() {
		return new ExcelUploadCommand();
	}

	@RequestMapping(value = "/show", method = RequestMethod.GET)
	public ModelAndView showUploadPage() {
		return new ModelAndView("admin/excel_import");
	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public ModelAndView uploadExcelFile(ExcelUploadCommand excelUploadCommand, BindingResult result, RedirectAttributes redirect) {
		try {
			MultipartFile myFile = excelUploadCommand.getMyFile();
			if (myFile == null || myFile.getBytes() == null || myFile.getBytes().length == 0) {
				messageUtil.addErrorMsg(redirect, "excel.upload.msg.noFileGiven");
				return new ModelAndView(REDIRECT_SHOW_PAGE);
			}

			if (!myFile.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
				messageUtil.addErrorMsg(redirect, "excel.upload.msg.noExcelFile");
				return new ModelAndView(REDIRECT_SHOW_PAGE);
			}

			excelImportService.importMatchesFromExcelAndSave(myFile.getBytes());

			messageUtil.addInfoMsg(redirect, "excel.upload.msg.saved");
		} catch (IOException | ExcelReadingException e) {
			LOG.error(e.getMessage(), e);
			messageUtil.addErrorMsg(redirect, "excel.upload.msg.failed", e.getMessage());
		}

		return new ModelAndView(REDIRECT_SHOW_PAGE);
	}
}