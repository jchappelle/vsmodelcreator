package at.vintagestory.modelcreator.gui.left;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex2d;
import static org.lwjgl.opengl.GL11.glVertex2i;

import java.util.ArrayList;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.TextureImpl;

import at.vintagestory.modelcreator.ModelCreator;
import at.vintagestory.modelcreator.enums.EnumFonts;
import at.vintagestory.modelcreator.interfaces.IElementManager;
import at.vintagestory.modelcreator.model.Element;
import at.vintagestory.modelcreator.model.Face;
import at.vintagestory.modelcreator.model.TextureEntry;

public class LeftUVSidebar extends LeftSidebar
{
	private IElementManager manager;

	private final int WIDTH = 110;

	private final Color BLACK_ALPHA = new Color(0, 0, 0, 0.75F);

	private int[] startX = { 0, 0, 0, 0, 0, 0 };
	private int[] startY = { 0, 0, 0, 0, 0, 0 };

	float[] brightnessByFace = new float[] { 1, 1, 1, 1, 1, 1 }; 
	
	public LeftUVSidebar(String title, IElementManager manager)
	{
		super(title);
		this.manager = manager;
	}

	@Override
	public void draw(int sidebarWidth, int canvasWidth, int canvasHeight, int frameHeight)
	{
		super.draw(sidebarWidth, canvasWidth, canvasHeight, frameHeight);

		if (ModelCreator.transparent) {
			GL11.glEnable(GL11.GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		}		

		if (ModelCreator.currentProject.EntityTextureMode) {
			drawSingleSquare(canvasHeight);
		} else {
			drawBoxSquares(canvasHeight);
		}
		
		if (ModelCreator.transparent) {
			GL11.glDisable(GL11.GL_BLEND);
		}
	}
	
	void drawSingleSquare(int canvasHeight) {
		double texWidth = ModelCreator.currentProject.TextureWidth;
		double texHeight = ModelCreator.currentProject.TextureHeight;
		
		double scaleX = 2;
		double scaleY = 2;
		
		if (ModelCreator.currentProject.Textures.size() > 0) {
			TextureEntry texEntry = ModelCreator.currentProject.Textures.get(ModelCreator.currentProject.Textures.keySet().iterator().next());
			scaleX = texEntry.Width / texWidth;
			scaleY = texEntry.Height / texHeight;
		}
		
		texWidth *= scaleX / 2;
		texHeight *= scaleY / 2;
		
		int texBoxWidth = (int)(2 * WIDTH);
		int texBoxHeight = (int)(texBoxWidth * texHeight / texWidth);

		double scaledTexWidth = texBoxWidth / texWidth;
		double scaledTexHeight = texBoxHeight / texHeight;
		
		glPushMatrix();
		{
			glTranslatef(10, 30, 0);

			glPushMatrix(); {
				
				glColor3f(1, 1, 1);

				if (ModelCreator.currentProject.rootElements.size() > 0) {
					Element elem = ModelCreator.currentProject.rootElements.get(0);	
					elem.getAllFaces()[0].bindTexture();
				}
				
				glLineWidth(1F);
				glBegin(GL_QUADS);
				{
					glTexCoord2f(0, 1);
					glVertex2i(0, texBoxHeight);
					
					glTexCoord2f(1, 1);
					glVertex2i(texBoxWidth, texBoxHeight);
					
					glTexCoord2f(1, 0);
					glVertex2i(texBoxWidth, 0);

					glTexCoord2f(0, 0);
					glVertex2i(0, 0);
				}
				glEnd();
				
				TextureImpl.bindNone();
				
				drawElementList(ModelCreator.currentProject.rootElements, scaledTexWidth, scaledTexHeight);
				
				
				glPopMatrix();
			}
		}
		glPopMatrix();
		
	}
	
	private void drawElementList(ArrayList<Element> elems, double scaledTexWidth, double scaledTexHeight)
	{
		Element selectedElem = ModelCreator.currentProject.SelectedElement;
		
		for (Element elem : elems) {
			Face[] faces = elem.getAllFaces();
			
			for (int i = 0; i < 6; i++) {
				if (!faces[i].isEnabled()) continue;
				
				Face face = faces[i];
				double u = face.getStartU();
				double v = face.getStartV();
				double uend = face.getEndU();
				double vend = face.getEndV();
				
				Color color = Face.getFaceColour(i);
				
				GL11.glColor4f(color.r * elem.brightnessByFace[i], color.g * elem.brightnessByFace[i], color.b * elem.brightnessByFace[i], 0.3f);
	
				glBegin(GL_QUADS);
				{
					glTexCoord2f(0, 1);
					glVertex2d(u * scaledTexWidth, vend * scaledTexHeight);
					
					glTexCoord2f(1, 1);
					glVertex2d(uend * scaledTexWidth, vend * scaledTexHeight);
					
					glTexCoord2f(1, 0);
					glVertex2d(uend * scaledTexWidth, v * scaledTexHeight);
	
					glTexCoord2f(0, 0);
					glVertex2d(u * scaledTexWidth, v * scaledTexHeight);
				}
				glEnd();
	
				
				glColor3f(0.5f, 0.5f, 0.5f);
				if (elem == selectedElem) {
					glColor3f(0f, 0f, 1f);
				}
				

	
				glBegin(GL_LINES);
				{
					glVertex2d(u * scaledTexWidth, v * scaledTexHeight);
					glVertex2d(u * scaledTexWidth, vend * scaledTexHeight);
	
					glVertex2d(u * scaledTexWidth, vend * scaledTexHeight);
					glVertex2d(uend * scaledTexWidth, vend * scaledTexHeight);
	
					glVertex2d(uend * scaledTexWidth, vend * scaledTexHeight);
					glVertex2d(uend * scaledTexWidth, v * scaledTexHeight);
	
					glVertex2d(uend * scaledTexWidth, v * scaledTexHeight);
					glVertex2d(u * scaledTexWidth, v * scaledTexHeight);
	
				}
				glEnd();
			}
			
			drawElementList(elem.ChildElements, scaledTexWidth, scaledTexHeight);
		}
		
	}

	void drawBoxSquares(int canvasHeight) {
		Element elem = manager.getCurrentElement();
		
		float[] bright = elem != null ? elem.brightnessByFace : brightnessByFace;
		
		
		
		glPushMatrix();
		{
			glTranslatef(10, 30, 0);

			int count = 0;

			for (int i = 0; i < 6; i++) {
				glPushMatrix(); {
					if (30 + i * (WIDTH + 10) + (WIDTH + 10) > canvasHeight) {
						glTranslatef(10 + WIDTH, count * (WIDTH + 10), 0);
						startX[i] = 20 + WIDTH;
						startY[i] = count * (WIDTH + 10) + 40;
						count++;
					}
					else
					{
						glTranslatef(0, i * (WIDTH + 10), 0);
						startX[i] = 10;
						startY[i] = i * (WIDTH + 10) + 40;
					}

					Color color = Face.getFaceColour(i);
					glColor3f(color.r * bright[i], color.g * bright[i], color.b * bright[i]);

					Face[] faces = null;
					if (elem != null) {
						faces = elem.getAllFaces();
					}

					if (faces != null)
					{
						faces[i].bindTexture();


						double scaleX = 0.5;
						double scaleY = 0.5;
						double texWidth = ModelCreator.currentProject.TextureWidth;
						double texHeight = ModelCreator.currentProject.TextureHeight; 
						
						texWidth *= scaleX;
						texHeight *= scaleY;

						TextureEntry entry = ModelCreator.currentProject.getTextureEntry(faces[i].getTextureName());
						
						if (entry != null) {
							texWidth = entry.Width / 2.0;
							texHeight = entry.Height / 2.0;
							scaleX = entry.Width / texWidth;
							scaleY = entry.Height / texHeight;
						}
						
						double scaledTexWidth = WIDTH / texWidth;
						double scaledTexHeight = WIDTH / texHeight;
						int scaledHeight = (int)(WIDTH * texHeight/texWidth);
						
						glBegin(GL_QUADS);
						{
							if (faces[i].isBinded())
								glTexCoord2f(0, 1);
							glVertex2i(0, scaledHeight);

							if (faces[i].isBinded())
								glTexCoord2f(1, 1);
							glVertex2i(WIDTH, scaledHeight);

							if (faces[i].isBinded())
								glTexCoord2f(1, 0);
							glVertex2i(WIDTH, 0);

							if (faces[i].isBinded())
								glTexCoord2f(0, 0);
							glVertex2i(0, 0);
						}
						glEnd();

						TextureImpl.bindNone();

						glColor3f(1, 1, 1);

						glBegin(GL_LINES);
						{
							glVertex2d(faces[i].getStartU() * scaledTexWidth, faces[i].getStartV() * scaledTexHeight);
							glVertex2d(faces[i].getStartU() * scaledTexWidth, faces[i].getEndV() * scaledTexHeight);

							glVertex2d(faces[i].getStartU() * scaledTexWidth, faces[i].getEndV() * scaledTexHeight);
							glVertex2d(faces[i].getEndU() * scaledTexWidth, faces[i].getEndV() * scaledTexHeight);

							glVertex2d(faces[i].getEndU() * scaledTexWidth, faces[i].getEndV() * scaledTexHeight);
							glVertex2d(faces[i].getEndU() * scaledTexWidth, faces[i].getStartV() * scaledTexHeight);

							glVertex2d(faces[i].getEndU() * scaledTexWidth, faces[i].getStartV() * scaledTexHeight);
							glVertex2d(faces[i].getStartU() * scaledTexWidth, faces[i].getStartV() * scaledTexHeight);

						}
						glEnd();

						glEnable(GL_BLEND);
						glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
						EnumFonts.BEBAS_NEUE_20.drawString(5, 5, Face.getFaceName(i), BLACK_ALPHA);
						glDisable(GL_BLEND);
					}
				}
				glPopMatrix();
			}
		}
		glPopMatrix();
	}
	
	

	private int lastMouseX, lastMouseY;
	private int selected = -1;
	private boolean grabbing = false;

	@Override
	public void handleInput(int canvasHeight)
	{
		super.handleInput(canvasHeight);

		if (Mouse.isButtonDown(0) | Mouse.isButtonDown(1))
		{
			if (!grabbing)
			{
				this.lastMouseX = Mouse.getX();
				this.lastMouseY = Mouse.getY();
				grabbing = true;
			}
		}
		else
		{
			grabbing = false;
		}

		if (grabbing)
		{
			int newMouseX = Mouse.getX();
			int newMouseY = Mouse.getY();

			int side = getFace(canvasHeight, newMouseX, newMouseY);
			if (side != -1 | selected != -1)
			{
				if (manager.getCurrentElement() != null)
				{
					Face face = manager.getCurrentElement().getAllFaces()[(selected != -1 ? selected : side)];

					int xMovement = (int) ((newMouseX - this.lastMouseX) / 6);
					int yMovement = (int) ((newMouseY - this.lastMouseY) / 6);

					if (xMovement != 0 | yMovement != 0)
					{
						if (Mouse.isButtonDown(0))
						{
							if ((face.getStartU() + xMovement) >= 0.0 && (face.getEndU() + xMovement) <= 16.0)
								face.moveTextureU(xMovement);
							if ((face.getStartV() - yMovement) >= 0.0 && (face.getEndV() - yMovement) <= 16.0)
								face.moveTextureV(-yMovement);
						}
						else
						{
							face.setAutoUVEnabled(false);

							if ((face.getEndU() + xMovement) <= 16.0)
								face.addTextureXEnd(xMovement);
							if ((face.getEndV() - yMovement) <= 16.0)
								face.addTextureYEnd(-yMovement);

							face.setAutoUVEnabled(false);
						}
						face.updateUV();

						if (xMovement != 0)
							this.lastMouseX = newMouseX;
						if (yMovement != 0)
							this.lastMouseY = newMouseY;
					}
					ModelCreator.updateValues();
				}
			}
		}
		else
		{
			selected = -1;
		}
	}

	public int getFace(int canvasHeight, int mouseX, int mouseY)
	{
		for (int i = 0; i < 6; i++)
		{
			if (mouseX >= startX[i] && mouseX <= startX[i] + WIDTH)
			{
				if ((canvasHeight - mouseY - 45) >= startY[i] && (canvasHeight - mouseY - 45) <= startY[i] + WIDTH)
				{
					return i;
				}
			}
		}
		return -1;
	}
}
