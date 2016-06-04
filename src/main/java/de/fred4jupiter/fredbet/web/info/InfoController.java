package de.fred4jupiter.fredbet.web.info;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import de.fred4jupiter.fredbet.domain.Info;
import de.fred4jupiter.fredbet.security.FredBetPermission;
import de.fred4jupiter.fredbet.service.InfoService;
import de.fred4jupiter.fredbet.web.MessageUtil;

@Controller
@RequestMapping("/info")
public class InfoController {

	private static final String PAGE_EDIT_INFO = "info/edit_info";

	private static final String INFO_CONTEXT_RULES = "rules";

	private static final String INFO_CONTEXT_PRICES = "prices";

	private static final String INFO_CONTEXT_MISC = "misc";

	@Autowired
	private MessageUtil messageUtil;

	@Autowired
	private InfoService infoService;

	@RequestMapping("/rules")
	public ModelAndView showRules() {
		Locale locale = LocaleContextHolder.getLocale();
		Info info = infoService.findBy(INFO_CONTEXT_RULES, locale);
		if (info == null) {
			info = infoService.saveInfoContent(INFO_CONTEXT_RULES, "", locale);
		}

		ModelAndView modelAndView = new ModelAndView("info/rules");
		modelAndView.addObject("textContent", info.getContent());
		return modelAndView;
	}

	@RequestMapping("/prices")
	public ModelAndView showPrices() {
		Locale locale = LocaleContextHolder.getLocale();
		Info info = infoService.findBy(INFO_CONTEXT_PRICES, locale);
		if (info == null) {
			info = infoService.saveInfoContent(INFO_CONTEXT_PRICES, "", locale);
		}

		ModelAndView modelAndView = new ModelAndView("info/prices");
		modelAndView.addObject("textContent", info.getContent());
		return modelAndView;
	}

	@RequestMapping("/misc")
	public ModelAndView showMiscellaneous() {
		Locale locale = LocaleContextHolder.getLocale();
		Info info = infoService.findBy(INFO_CONTEXT_MISC, locale);
		if (info == null) {
			info = infoService.saveInfoContent(INFO_CONTEXT_MISC, "", locale);
		}

		ModelAndView modelAndView = new ModelAndView("info/misc");
		modelAndView.addObject("textContent", info.getContent());
		return modelAndView;
	}

	@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_EDIT_INFOS + "')")
	@RequestMapping("/editinfo/{name}")
	public ModelAndView editInfo(@PathVariable("name") String name) {
		Locale locale = LocaleContextHolder.getLocale();
		Info info = infoService.findBy(name, locale);
		if (info == null) {
			info = infoService.saveInfoContent(name, "", locale);
		}

		ModelAndView modelAndView = new ModelAndView(PAGE_EDIT_INFO);
		InfoCommand infoCommand = new InfoCommand();
		infoCommand.setName(info.getPk().getName());
		infoCommand.setTextContent(info.getContent());
		modelAndView.addObject("infoCommand", infoCommand);
		return modelAndView;
	}

	@PreAuthorize("hasAuthority('" + FredBetPermission.PERM_EDIT_INFOS + "')")
	@RequestMapping(value = "/editinfo", method = RequestMethod.POST)
	public ModelAndView saveEditedInfo(InfoCommand infoCommand, ModelMap modelMap) {
		Locale locale = LocaleContextHolder.getLocale();
		infoService.saveInfoContent(infoCommand.getName(), infoCommand.getTextContent(), locale);

		ModelAndView modelAndView = new ModelAndView(PAGE_EDIT_INFO);
		modelAndView.addObject("infoCommand", infoCommand);
		messageUtil.addInfoMsg(modelMap, "info.msg.edit.saved");

		return modelAndView;
	}

}