package codegeeks.spring5recipe.controllers;

import codegeeks.spring5recipe.commands.IngredientCommand;
import codegeeks.spring5recipe.commands.RecipeCommand;
import codegeeks.spring5recipe.commands.UnitOfMeasureCommand;
import codegeeks.spring5recipe.services.IngredientService;
import codegeeks.spring5recipe.services.RecipeService;
import codegeeks.spring5recipe.services.UnitOfMeasureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
public class IngredientController {
    private final RecipeService recipeService;
    private final IngredientService ingredientService;
    private final UnitOfMeasureService unitOfMeasureService;

    public IngredientController(RecipeService recipeService, IngredientService ingredientService, UnitOfMeasureService unitOfMeasureService) {
        this.recipeService = recipeService;
        this.ingredientService = ingredientService;
        this.unitOfMeasureService = unitOfMeasureService;
    }

    @GetMapping("recipe/{recipeId}/ingredients")
    public String listIngredients(@PathVariable String recipeId, Model model) {
        log.debug("Getting ingredient list for recipe id: " + recipeId);
        model.addAttribute("recipe", recipeService.findCommandById(new Long(recipeId)));
        return "recipe/ingredient/list";
    }

    @GetMapping("recipe/{recipeId}/ingredient/{ingredientId}/show")
    public String showIngredient(@PathVariable String recipeId,
                                 @PathVariable String ingredientId,
                                 Model model) {
        log.debug("Getting ingredient id " + ingredientId + " for recipe id: " + recipeId);
        model.addAttribute("ingredient", ingredientService.findByRecipeIdAndIngredientId(new Long(recipeId), new Long(ingredientId)));
        return "recipe/ingredient/show";
    }

    @GetMapping("recipe/{recipeId}/ingredient/{ingredientId}/update")
    public String updateIngredient(@PathVariable String recipeId,
                                   @PathVariable String ingredientId,
                                   Model model) {
        log.debug("Updating ingredient id " + ingredientId + " for recipe id: " + recipeId);
        model.addAttribute("ingredient", ingredientService.findByRecipeIdAndIngredientId(new Long(recipeId), new Long(ingredientId)));
        model.addAttribute("unitOfMeasureList", unitOfMeasureService.listAllUnitOfMeasures());
        return "recipe/ingredient/ingredientform";
    }

    @GetMapping("recipe/{recipeId}/ingredient/{ingredientId}/delete")
    public String deleteIngredient(@PathVariable String recipeId,
                                   @PathVariable String ingredientId) {
        log.debug("Deleting ingredient id " + ingredientId + " for recipe id: " + recipeId);
        ingredientService.deleteByRecipeIdAndIngredientId(Long.valueOf(recipeId), Long.valueOf(ingredientId));
        return "redirect:"
                + "/recipe/" + recipeId
                + "/ingredients";
    }

    @GetMapping("recipe/{recipeId}/ingredient/new")
    public String createIngredient(@PathVariable String recipeId,
                                   Model model) {
        log.debug("Create ingredient for recipe id: " + recipeId);

        RecipeCommand recipeCommand = recipeService.findCommandById(Long.valueOf(recipeId));
        // todo raise exception if null
        IngredientCommand ingredientCommand = new IngredientCommand();
        ingredientCommand.setRecipeId(recipeCommand.getId());
        ingredientCommand.setUnitOfMeasure(new UnitOfMeasureCommand());

        model.addAttribute("ingredient", ingredientCommand);
        model.addAttribute("unitOfMeasureList", unitOfMeasureService.listAllUnitOfMeasures());
        return "recipe/ingredient/ingredientform";
    }

    @PostMapping("recipe/{recipeId}/ingredient")
    public String saveOrUpdate(@ModelAttribute IngredientCommand command) {
        IngredientCommand savedCommand = ingredientService.saveIngredientCommand(command);
        log.debug("saved recipe id: " + savedCommand.getRecipeId());
        log.debug("saved ingredient id: " + savedCommand.getId());
        return "redirect:" +
                "/recipe/" + savedCommand.getRecipeId() +
                "/ingredient/" + savedCommand.getId() +
                "/show";
    }
}
