package com.bezkoder.spring.thymeleaf;

import com.bezkoder.spring.thymeleaf.controller.TutorialController;
import com.bezkoder.spring.thymeleaf.entity.Tutorial;
import com.bezkoder.spring.thymeleaf.repository.TutorialRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TutorialControllerTest {

    @Mock
    private TutorialRepository tutorialRepository;

    @InjectMocks
    private TutorialController tutorialController;

    @Test
    public void testGetAll() {
        Model model = new BindingAwareModelMap();
        List<Tutorial> tutorials = new ArrayList<>();
        tutorials.add(new Tutorial("Test Title", "Test Description", 3, true));

        when(tutorialRepository.findAll()).thenReturn(tutorials);

        String viewName = tutorialController.getAll(model, null);

        assertEquals("tutorials", viewName);
        List<Tutorial> modelTutorials = (List<Tutorial>) model.getAttribute("tutorials");
        assertEquals(1, modelTutorials.size());
        assertEquals("Test Title", modelTutorials.get(0).getTitle());
    }

    @Test
    public void testGetAllWithKeyword() {
        Model model = new BindingAwareModelMap();
        List<Tutorial> tutorials = new ArrayList<>();
        tutorials.add(new Tutorial("Test Title", "Test Description", 3, true));

        when(tutorialRepository.findByTitleContainingIgnoreCase(anyString())).thenReturn(tutorials);

        String viewName = tutorialController.getAll(model, "Test");

        assertEquals("tutorials", viewName);
        List<Tutorial> modelTutorials = (List<Tutorial>) model.getAttribute("tutorials");
        assertEquals(1, modelTutorials.size());
        assertEquals("Test Title", modelTutorials.get(0).getTitle());
        assertEquals("Test", model.getAttribute("keyword"));
    }

    @Test
    public void testAddTutorial() {
        Model model = new BindingAwareModelMap();

        String viewName = tutorialController.addTutorial(model);

        assertEquals("tutorial_form", viewName);
        Tutorial tutorial = (Tutorial) model.getAttribute("tutorial");
        assertEquals(true, tutorial.isPublished());
        assertEquals("Create new Tutorial", model.getAttribute("pageTitle"));
    }

    @Test
    public void testSaveTutorial() {
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        Tutorial tutorial = new Tutorial("Test Title", "Test Description", 3, true);

        String viewName = tutorialController.saveTutorial(tutorial, redirectAttributes);

        assertEquals("redirect:/tutorials", viewName);
        verify(tutorialRepository, times(1)).save(tutorial);
        assertEquals("The Tutorial has been saved successfully!", redirectAttributes.getFlashAttributes().get("message"));
    }

    @Test
    public void testEditTutorial() {
        Model model = new BindingAwareModelMap();
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();
        Tutorial tutorial = new Tutorial("Test", "Test Description", 3, true);

        when(tutorialRepository.findById(anyInt())).thenReturn(Optional.of(tutorial));

        String viewName = tutorialController.editTutorial(1, model, redirectAttributes);

        assertEquals("tutorial_form", viewName);
        assertEquals(tutorial, model.getAttribute("tutorial"));
        assertEquals("Edit Tutorial (ID: 1)", model.getAttribute("pageTitle"));
    }

    @Test
    public void testDeleteTutorial() {
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        doNothing().when(tutorialRepository).deleteById(anyInt());

        String viewName = tutorialController.deleteTutorial(1, new BindingAwareModelMap(), redirectAttributes);

        assertEquals("redirect:/tutorials", viewName);
        verify(tutorialRepository, times(1)).deleteById(1);
        assertEquals("The Tutorial with id=1 has been deleted successfully!", redirectAttributes.getFlashAttributes().get("message"));
    }

    @Test
    public void testUpdateTutorialPublishedStatus() {
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        doNothing().when(tutorialRepository).updatePublishedStatus(anyInt(), anyBoolean());

        String viewName = tutorialController.updateTutorialPublishedStatus(1, true, new BindingAwareModelMap(), redirectAttributes);

        assertEquals("redirect:/tutorials", viewName);
        verify(tutorialRepository, times(1)).updatePublishedStatus(1, true);
        assertEquals("The Tutorial id=1 has been published", redirectAttributes.getFlashAttributes().get("message"));
    }
}
